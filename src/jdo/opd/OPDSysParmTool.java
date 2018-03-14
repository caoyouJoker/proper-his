package jdo.opd;

import java.sql.Timestamp;
import java.util.GregorianCalendar;

import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

/**
 *
 * <p>
 * Title: 门诊医生站参数tool
 *
 * <p>
 * Description: 门诊医生站参数tool
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 *
 * <p>
 * Company: javahis
 *
 * @author ehui 20081224
 * @version 1.0
 */
public class OPDSysParmTool extends TJDOTool {
	/**
	 * 实例
	 */
	public static OPDSysParmTool instanceObject;
	public static final String SQL="SELECT * FROM OPD_SYSPARM";
	public TParm parm;

	/**
	 * 得到实例
	 *
	 * @return OPDSysParmTool
	 */
	public static OPDSysParmTool getInstance() {
		if (instanceObject == null)
			instanceObject = new OPDSysParmTool();
		return instanceObject;
	}

	/**
	 * 构造器
	 */
	public OPDSysParmTool() {
		setModuleName("opd\\OPDSysParmModule.x");
		parm=new TParm(TJDODBTool.getInstance().select(SQL));
		onInit();
	}

	/**
	 * 查出参数档中的儿童的岁数的定义，例如6岁以下算儿童，然后返回 生日+6>=sysdate，如果成立，则是儿童，反之，就是大人
	 *
	 * @param age
	 *            病患出生日期
	 * @return boolean 真：儿童，假：大人
	 */
	 public boolean isChild(Timestamp age) {// wanglong modify 20150119
        return isChild(age, SystemTool.getInstance().getDate());
    }

    /**
     * 是否为小孩
     * @param age
     * @param sysDate
     * @return
     */
    public boolean isChild(Timestamp age, Timestamp sysDate) {// wanglong add 20150119
        if (age == null) {
            err("ERR:age参数为空!");
            return false;
        }
        if (sysDate == null) {
            sysDate = SystemTool.getInstance().getDate();
        }
        TParm data = selectAll();
        if (data.getErrCode() < 0) {
            err("ERR:" + data.getErrCode() + data.getErrText() + data.getErrName());
            return false;
        }
        int year = 0;
        year = data.getInt("AGE", 0);
        if (year < 0) {
            err("ERR:请核对OPD_SYSPARM表中AGE字段，给定儿童测试年限错误!");
            return false;
        }
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(age);
        gc.add(GregorianCalendar.YEAR, year);
        return gc.getTime().getTime() > sysDate.getTime();
    }
    
	/**
	 * 全字段检索OpdSysParm
	 *
	 * @return TParm
	 */
	public TParm selectAll() {
		TParm result = query("isChild");
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
		}
		return result;
	}

	/**
	 * 更新一条数据
	 *
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updataData(TParm parm) {
		TParm result = new TParm();
		result = update("updatedata", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
		}
		return result;

	}
	/**
	 * 返回中医常用用法
	 * @return String
	 */
	public String getGRouteCode(){
		String routeCode="";
		routeCode=parm.getValue("G_ROUTE_CODE",0);
//		//System.out.println("SYSpARM==============================routeCode"+routeCode);
		return routeCode;
	}
	/**
	 * 返回中医常用频次
	 * @return String
	 */
	public String getGfreqCode(){
		String freqCode;
		freqCode=parm.getValue("G_FREQ_CODE",0);
//		//System.out.println("SYSpARM==============================freqCode"+freqCode);
		return freqCode;
	}
	/**
	 * 返回中医常用煎药方式
	 * @return String
	 */
	public String getGdctAgent(){
		String dctAgent;
		dctAgent=parm.getValue("G_DCTAGENT_CODE",0);
		return dctAgent;
	}
	/**
	 * 返回全表数据
	 * @return TParm
	 */
	public TParm getSysParm(){
		if(parm==null||parm.getErrCode()!=0)
			parm=new TParm(TJDODBTool.getInstance().select(SQL));
		return parm;
	}
        /**
         * 获取急诊限定看诊天数
         * @return int
         */
        public int getEDays(){
            int days;
            days=parm.getInt("E_DAYS",0);
            return days;
        }
}
