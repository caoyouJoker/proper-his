package jdo.reg;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import java.sql.Timestamp;
import com.dongyang.util.StringTool;
import com.javahis.util.JavaHisDebug;
import java.util.ArrayList;
import com.dongyang.jdo.TJDODBTool;
/**
 *
 * <p>Title:挂号时段工具类 </p>
 *
 * <p>Description:挂号时段工具类 </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.08.22
 * @version 1.0
 */
public class SessionTool extends TJDOTool{
    /**
     * 实例
     */
    public static SessionTool instanceObject;
    /**
     * 得到实例
     * @return SessionTool
     */
    public static SessionTool getInstance() {
        if (instanceObject == null)
            instanceObject = new SessionTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public SessionTool() {
        setModuleName("reg\\REGSessionModule.x");

        onInit();
    }
    /**
     * 新增挂号时段
     * @param parm TParm
     * @return TParm
     */
    public TParm insertdata(TParm parm) {
        TParm result = new TParm();
        result = update("insertdata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 更新挂号时段
     * @param parm TParm
     * @return TParm
     */
    public TParm updatedata(TParm parm) {
        TParm result = update("updatedata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 根据门急住别,区域查询挂号时段(右忽略)
     * @param admType String
     * @param regionCode String
     * @param sessionCode String
     * @return TParm
     */
    public TParm selectdata(String admType,String regionCode,String sessionCode) {
        TParm parm = new TParm();
//        admType += "%";
        if (!"".equals(admType))
            parm.setData("ADM_TYPE", admType);
//        regionCode += "%";
        if (!"".equals(regionCode))
            parm.setData("REGION_CODE", regionCode);
//        sessionCode += "%";
        if (!"".equals(sessionCode))
            parm.setData("SESSION_CODE", sessionCode);
//        System.out.println("入参"+parm);
        TParm result = query("selectdata", parm);

        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 删除选中挂号时段
     * @param admType String
     * @param regionCode String
     * @param sessionCode String
     * @return TParm
     */
    public TParm deletedata(String admType,String regionCode,String sessionCode) {
        TParm parm = new TParm();
        parm.setData("ADM_TYPE", admType);
        parm.setData("REGION_CODE", regionCode);
        parm.setData("SESSION_CODE", sessionCode);

        TParm result = update("deletedata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }


    /**
     * 取得门急住别combo信息
     * @return TParm
     */
    public TParm getadmTypeCombo() {
        TParm parm = new TParm();
        return query("getadmTypeCombo", parm);
    }

    /**
     * 取得区域combo信息
     * @return TParm
     */
    public TParm getRegionCombo() {
        TParm parm = new TParm();
        return query("getRegionCombo", parm);
    }
    /**
     * 判断是否存在挂号时段
     * @param sessionCode String 挂号时段
     * @return boolean TRUE 存在 FALSE 不存在
     */
    public boolean existsSession(String sessionCode){
        TParm parm = new TParm();
        parm.setData("SESSION_CODE",sessionCode);
        return getResultInt(query("existsSession",parm),"COUNT") > 0;
    }
    /**
     * 取得挂号时段起讫
     * @param admType String
     * @param regionCode String
     * @return String
     */
    public String getDefSessionNow(String admType ,String regionCode) {
        String whereRegion = "";
        if(!"".equals(regionCode))
            whereRegion = "    AND REGION_CODE = '"+regionCode+"' ";
        //yanjing 添加了秒20130502
        String sql =
            " SELECT SESSION_CODE "+
            "   FROM REG_SESSION "+
            "  WHERE ADM_TYPE='"+admType+"' "+
            whereRegion +
            "    AND (       TO_CHAR (SYSDATE, 'HH24:MI:SS') BETWEEN START_REG_TIME "+
            "                                                 AND END_REG_TIME "+
            "            AND (START_REG_TIME < END_REG_TIME) "+
            "            OR (    (   TO_CHAR (SYSDATE, 'HH24:MI:SS') BETWEEN '00:00:00' AND END_REG_TIME "+
            "                    OR TO_CHAR (SYSDATE, 'HH24:MI:SS') BETWEEN START_REG_TIME "+
            "                                                        AND '24:00:00') "+
            "            AND (START_REG_TIME > END_REG_TIME) ) ) ";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        String sessionCode = result.getValue("SESSION_CODE",0);
        return sessionCode;
    }
    /**
     * 取得看诊时段起讫
     * @param admType String
     * @param regionCode String
     * @return String
     */
    public String getDrSessionNow(String admType,String regionCode) {
        TParm parm = new TParm();
        parm.setData("ADM_TYPE", admType);
        if (!"".equals(regionCode))
            parm.setData("REGION_CODE", regionCode);
        return getResultString(query("getDrSessionNow", parm), "SESSION_CODE");
    }
    /**
     * 得到时段编号列表
     * @param admType String
     * @param regionCode String
     * @return String[]
     */
    public String[] getSessionCode(String admType,String regionCode)
    {
        TParm parm = new TParm();
        parm.setData("ADM_TYPE",admType);
        if(!"".equals(regionCode))
            parm.setData("REGION_CODE",regionCode);
        TParm result = query("getSessionCode",parm);
        if(result.getErrCode() < 0)
            return null;
        ArrayList list = new ArrayList();
        int count = result.getCount();
        for(int i = 0;i < count;i ++)
            list.add(result.getValue("SESSION_CODE",i));
        return (String[])list.toArray(new String[]{});
    }
    /**
     * 根据门急别和时段代码查询时段信息
     * @param admType String
     * @param session_code String
     * @param regionCode String
     * @return TParm
     */
    public TParm getSessionInfoByCode(String admType,String session_code,String regionCode){
        TParm parm = new TParm();
        parm.setData("ADM_TYPE",admType);
        parm.setData("SESSION_CODE",session_code);
        if(!"".equals(regionCode))
            parm.setData("REGION_CODE",regionCode);
        TParm result = query("getSessionInfoByCode",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 判断某个时段是否是跨天的
     * @param admType String
     * @param session_code String
     * @param regionCode String
     * @return boolean
     */
    public boolean isCrossDays(String admType,String session_code,String regionCode){
        TParm session = this.getSessionInfoByCode(admType,session_code,regionCode);
        boolean result = false;
        if(session.getCount()>0){
            if(session.getValue("START_CLINIC_TIME",0).compareTo(session.getValue("END_CLINIC_TIME",0))>0){
                result = true;
            }
        }
        return result;
    }
    /**
     * 根据时段判断应该显示的日期（针对于晚班夸0点的问题，跨过0点的晚班应该显示前一天的日期）
     * @param admType String
     * @param session_code String
     * @param regionCode String
     * @return Timestamp
     */
    public Timestamp getDateForSession(String admType,String session_code,String regionCode){
        //当前服务器时间
        Timestamp date = TJDODBTool.getInstance().getDBTime();
        //判断该时段是否跨天
        if(!this.isCrossDays(admType,session_code,regionCode)){
            //如果不跨天返回当前服务器时间
            return date;
        }
        //如果跨天
        TParm session = this.getSessionInfoByCode(admType,session_code,regionCode);//时段信息
        String time = StringTool.getString(date,"HH:mm:ss");
        //判断当前时间是否已经超过就诊时间
        if(time.compareTo(session.getValue("END_CLINIC_TIME",0))<0){
            //如果没有超过就诊时间 那么返回前一天的日期
            date = StringTool.rollDate(date,-1);
        }
        return date;
    }
    public static void main(String args[])
    {
        JavaHisDebug.initClient();
       // System.out.println(StringTool.getString(SessionTool.getInstance().getSessionCode("E","")));
    }
/**
 * 获取挂号开始时间
 *yanjing20130503
 */
public String getStartTime(String admType,String sessionCode){
	String whereSession = "";
	if (!"".equals(sessionCode)) 
		 whereSession = "    AND SESSION_CODE = '"+sessionCode+"' ";
			String sql = "SELECT START_REG_TIME"+
				" FROM REG_SESSION"+
				" WHERE ADM_TYPE='"+admType+"'"+
						whereSession;
			TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		    String startTime = result.getValue("START_REG_TIME",0);
	return startTime;
	
}
/**
 * 获取挂号结束时间
 * yanjing 20130503
 */
public String getEndTime(String admType,String sessionCode){
	String whereSession = "";
	if (!"".equals(sessionCode)) 
		 whereSession = "    AND SESSION_CODE = '"+sessionCode+"' ";
			String sql = "SELECT END_REG_TIME"+
				" FROM REG_SESSION"+
				" WHERE ADM_TYPE='"+admType+"'"+
						whereSession;
			TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		    String endTime = result.getValue("END_REG_TIME",0);
	return endTime;
}
	
}
