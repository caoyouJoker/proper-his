/**
 *
 */
package jdo.sys;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.StringTool;

/**
 *
 * <p>Title: 区域工具包</p>
 *
 * <p>Description:区域工具包 </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author ehui 2008.9.3
 * @version 1.0
 */
public class SYSRegionTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static SYSRegionTool instanceObject;
    /**
     * 得到实例
     * @return SYSRegionTool
     */
    public static SYSRegionTool getInstance() {
        if (instanceObject == null)
            instanceObject = new SYSRegionTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public SYSRegionTool() {
        setModuleName("sys\\SYSRegionModule.x");
        onInit();
    }

    /**
     * 得到医院主院区的简称
     * @return String
     */
    public String selRegionABN() {
        String regionABN = "";
        TParm result = query("selectRegionABN");
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return regionABN;
        }
        regionABN=result.getValue("REGION_CHN_ABN",0);
        return regionABN;
    }

    /**
     * 根据区域代码查询数据
     * @param REGION_CODE String 区域代码
     * @return TParm
     */
    public TParm selectdata(String REGION_CODE) {
        TParm parm = new TParm();
        parm.setData("REGION_CODE", REGION_CODE);
        TParm result = query("selectdata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 查询页面flag的顺序,一遍拼出20位的形如“yyyyynnnnn”的数据作为state_list的字段值
     * @param GROUP_ID
     * @return
     */
    public TParm getflagorder(String GROUP_ID) {
        TParm parm = new TParm();
        parm.setData("GROUP_ID", GROUP_ID);
        TParm result = query("getflagorder", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    public TParm selectall()
    {
        TParm result = query("selectall");
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 得到ip所属区域
     * @param ip String
     * @return String
     */
    public String getRegionByIP(String ip)
    {
        TParm result = selectall();
        for(int i = 0;i < result.getCount();i++)
        {
            String ipStart = result.getValue("IP_RANGE_START",i);
            String ipEnd = result.getValue("IP_RANGE_END",i);
            if(StringTool.isIPBetween(ipStart,ipEnd,ip))
                return result.getValue("REGION_CODE",i);
        }
        return "";
    }
    public static void main(String args[])
    {
        SYSRegionTool.getInstance().selectall();
    }

    /**
     * 区域权限管理，登录区域为空值时,显示区域可以选择
     * ====pangben modify 20110422
     */
    public boolean getRegionIsEnabled(String regionCode) {
        if (!"".equals(regionCode) && null != regionCode)
            return false;
        else
            return true;
    }
}
