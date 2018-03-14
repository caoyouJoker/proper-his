package jdo.sta;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;
import java.sql.Timestamp;
import com.dongyang.util.StringTool;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.dongyang.ui.TTextFormat;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import jdo.sys.SystemTool;

/**
 * <p>Title: STA对外公用方法</p>
 *
 * <p>Description: STA对外公用方法</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-6-12
 * @version 1.0
 */
public class STATool
    extends TJDOTool {
    /**
     * 实例
     */
    public static STATool instanceObject;

    /**
     * 得到实例
     * @return RegMethodTool
     */
    public static STATool getInstance() {
        if (instanceObject == null)
            instanceObject = new STATool();
        return instanceObject;
    }

    public STATool() {
        setModuleName("sta\\STAModule.x");
        onInit();
    }
    /**
     * 根据科室等级查询中间对照科室
     * @param level String[] 字符串数组存放 科室等级 格式如 {"1","2","3"}
     * @return TParm
     * ===============pangben modify 20110523 添加区域参数
     */
    public TParm getDeptByLevel(String[] level,String regionCode){
        //===============pangben modify 20110523 start
        String region = "";
        if (null != regionCode && regionCode.length() > 0)
            region = "AND REGION_CODE='" + regionCode + "' ";
         //===============pangben modify 20110523 stop
        String sql = "SELECT DISTINCT DEPT_CODE,DEPT_DESC,DEPT_LEVEL,OE_DEPT_CODE,IPD_DEPT_CODE FROM STA_OEI_DEPT_LIST ";
        if(level.length>0){
            //如果数组长度大于零 加入where条件
            sql += " WHERE DEPT_LEVEL IN (";
            //加入科室条件
            for(int i=0;i<level.length;i++){
                sql += "'"+level[i]+"',";
            }
            //去除结尾的逗号
            if(sql.endsWith(","))
                sql = sql.substring(0,sql.length()-1);
            sql += ") "+region+" ORDER BY TO_NUMBER(DEPT_CODE)";//==========pangben modify 20110523
        }else{
            sql+=" WHERE "+region.substring(region.indexOf("D")+1,region.length()); //==========pangben modify 20110523
        }
        TParm result = new TParm();
        result.setData(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 根据科室等级查询中间对照科室
     * @param level String[] 字符串数组存放 科室等级 格式如 {"1","2","3"}
     * @param DEPT_TYPE String  科室类别 OE：门诊科室   IPD：住院科室
     * @return TParm
     */
    public TParm getDeptByLevel(String[] level,String DEPT_TYPE,String regionCode){
        //==========pangben modify 20110523 start
        String region="";
        if(null!=regionCode&&regionCode.length()>0)
            region="AND  REGION_CODE='"+regionCode+"' ";
        //==========pangben modify 20110523 stop
        String sql = "SELECT DISTINCT DEPT_CODE,DEPT_DESC,DEPT_LEVEL,OE_DEPT_CODE,IPD_DEPT_CODE FROM STA_OEI_DEPT_LIST";
        if(level.length>0){
            //如果数组长度大于零 加入where条件
            sql += " WHERE DEPT_LEVEL IN (";
            //加入科室条件
            for(int i=0;i<level.length;i++){
                sql += "'"+level[i]+"',";
            }
            //去除结尾的逗号
            if(sql.endsWith(","))
                sql = sql.substring(0,sql.length()-1);
            sql += ")";
            if("OE".equals(DEPT_TYPE)){
                sql += " AND OE_DEPT_CODE IS NOT NULL ";
            }else if("IPD".equals(DEPT_TYPE)){
                sql += " AND IPD_DEPT_CODE IS NOT NULL ";
            }
            sql += region + " ORDER BY TO_NUMBER(DEPT_CODE)";
        } else {
            sql += " WHERE " + region.substring(region.indexOf("D")+1,region.length()); //==========pangben modify 20110523
        }
        TParm result = new TParm();
        result.setData(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    public Map getIBSChargeName() {
		TParm result = this.query("getIBSChargeName");
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return null;
		}
		String seq = "";
		String c_name = "";
		Map map = new HashMap();
		for (int i = 1; i <= 30; i++) {
			c_name = "CHARGE";
			if (i < 10)// I小于10 补零
				seq = "0" + i;
			else
				seq = "" + i;
			c_name += seq;
			map.put(c_name, result.getData(c_name, 0));
		}
		return map;
	}
    /**
     * 获取指定月份的最后一天的日期
     * @param date String 格式 YYYYMM
     * @return Timestamp
     */
    public Timestamp getLastDayOfMonth(String date){
        if(date.trim().length()<=0){
            return null;
        }
        Timestamp time = StringTool.getTimestamp(date,"yyyyMM");
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(time.getTime()));
        // 当前月＋1，即下个月
        cal.add(cal.MONTH, 1);
        // 将下个月1号作为日期初始值
        cal.set(cal.DATE, 1);
        // 下个月1号减去一天，即得到当前月最后一天
        cal.add(cal.DATE, -1);
        Timestamp result = new Timestamp(cal.getTimeInMillis());
        return result;
    }
    /**
     * 根据指定的月份加减计算需要的月份
     * @param Month String 制定月份 格式:yyyyMM
     * @param num int 加减的数量 以月为单位
     * @return String
     */
    public String rollMonth(String Month,int num){
        if(Month.trim().length()<=0){
            return "";
        }
        Timestamp time = StringTool.getTimestamp(Month,"yyyyMM");
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(time.getTime()));
        // 当前月＋num
        cal.add(cal.MONTH, num);
        // 将下个月1号作为日期初始值
        cal.set(cal.DATE, 1);
        Timestamp month = new Timestamp(cal.getTimeInMillis());
        String result = StringTool.getString(month,"yyyyMM");
        return result;
    }
    /**
     * 检查某表的提交状态
     * @param tablName String 表名
     * @param STA_DATE String 主键条件
     * @param condition String 其他条件
     * @param regionCode String 区域条件
     * @return int  0：没有数据；1：有数据但是没有提交；2：已经提交；-1：错误
     * =============pangben modify 20110519 添加区域条件
     */
    public int checkCONFIRM_FLG(String tableName,String STA_DATE,String condition,String regionCode){
        int re=-1;
        if(tableName.trim().length()<=0||STA_DATE.trim().length()<=0){
            System.out.println("STATool.checkCONFIRM_FLG====>:参数不能为空");
            return -1;
        }
        //==============pangben modify 20110519 start
        String region="";
        if(null!=regionCode && regionCode.length()>0)
            region=" AND REGION_CODE='"+regionCode+"'";
        //==============pangben modify 20110519 stop
        String sql = "select * from "+tableName+" where STA_DATE='"+STA_DATE+"'"+region;
        //如果存在其他条件 拼接SQL语句
        if(condition.trim().length()>0){
            sql += " AND " + condition;
        }
        TParm result = new TParm();
        result.setData(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return -1;
        }
        //判断是否有数据
        if(result.getCount("STA_DATE")>0){
            re = 2;//假设所有数据已经提交
            for(int i=0;i<result.getCount("STA_DATE");i++){
                //循环结果集 如果数据中存在没有提交的数据 返回值为1
                if(!result.getValue("CONFIRM_FLG",i).equals("Y")){
                    re = 1;
                    break;
                }
            }
        }else{//没有行数，没有数据
            re = 0;
        }
        return re;
    }
    /**
     * 检查某表的提交状态
     * @param tablName String 表名
     * @param STA_DATE String 主键条件
     * @param regionCode String 区域条件
     * @return int
     * ============pangben modify 20110519 添加区域参数
     */
    public int checkCONFIRM_FLG(String tableName,String STA_DATE,String regionCode){
        return this.checkCONFIRM_FLG(tableName,STA_DATE,"",regionCode);
    }
    /**
     * 获取某月的天数
     * @param date String  格式：yyyyMM
     * @return int
     */
    public int getDaysOfMonth(String date){
        Calendar rightNow = Calendar.getInstance();
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyyMM");
        try {
            rightNow.setTime(simpleDate.parse(date));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        int days = rightNow.getActualMaximum(Calendar.DAY_OF_MONTH);
        return days;
    }
    /**
     * 获取数据库时间的上一个月份
     * @param data String
     * @return String
     */
    public Timestamp getLastMonth(){
        String now = StringTool.getString(SystemTool.getInstance().getDate(),"yyyyMM");
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyyMM");
        Calendar rightNow = Calendar.getInstance();
        try {
            rightNow.setTime(simpleDate.parse(now));
            rightNow.add(rightNow.MONTH,-1);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        Timestamp result = new Timestamp(rightNow.getTimeInMillis());
        return result;
    }
}
