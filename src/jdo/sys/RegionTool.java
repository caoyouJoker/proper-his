package jdo.sys;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;

public class RegionTool extends TJDOTool{
    /**
     * 实例
     */
    public static RegionTool instanceObject;
    /**
     * 得到实例
     * @return RegionTool
     */
    public static RegionTool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new RegionTool();
        return instanceObject;
    }
    /**
     * 构造器
     */
    public RegionTool()
    {
        setModuleName("sys\\SYSRegionModule.x");
        onInit();
    }
    public TParm getFlgMap()
    {
        return DictionaryTool.getInstance().getGroupList("SYS_REGION_FLG");
    }
    public boolean getFlg(String regionCode,String flgName)
    {
        TParm parm = getFlgMap();
        return true;
    }
    public boolean isEmr(String regionCode)
    {
        return getFlg(regionCode,"EMR_FLG");
    }

}
