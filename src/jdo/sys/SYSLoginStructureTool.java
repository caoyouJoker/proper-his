package jdo.sys;

import com.dongyang.jdo.TJDODBTool;
import com.dongyang.data.TParm;
import java.util.Map;

/**
 *
 * <p>Title: 登录专用程序加载树数据采集对象(穿透技术)</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class SYSLoginStructureTool extends TJDODBTool{
    /**
     * 得到主程列表根据权限(Tree专用)
     * @param roleCode String 权限编号
     * @return TParm
     */
    public TParm getStructureTreeForRole(String roleCode)
    {
        return new TParm(getStructureTreeForRole("SYS_SUBSYSTEM",roleCode));
    }
    /**
     * 得到主程列表根据权限(Tree专用)
     * @param groupID String 组编号
     * @param roleCode String 权限编号
     * @return Map
     */
    public Map getStructureTreeForRole(String groupID,String roleCode)
    {
        if(isClientlink())
            return (Map)callServerMethod(groupID,roleCode);
        TParm parm = new TParm(getStructureForRole(groupID,roleCode));
        int count = parm.getCount();
        for(int i = 0;i < count;i++)
        {
            String id = parm.getValue("ID",i);
            parm.addData("CHILD",getStructureTreeForRole(id,roleCode));
        }
        return parm.getData();
    }
    /**
     * 得到主程列表根据权限
     * @param groupID String 组编号
     * @param roleCode String 权限编号
     * @return Map
     */
    public Map getStructureForRole(String groupID,String roleCode)
    {
        String sql =
            "SELECT GROUP_ID,ID,CHN_DESC AS NAME,ENG_DESC AS ENNAME,TYPE,PARENT_ID,STATE,DATA" +
            "     FROM SYS_DICTIONARY,SYS_ROLE_POPEDOM" +
            "    WHERE GROUP_ID='" + groupID + "'" +
            "      AND GROUP_ID=SYS_ROLE_POPEDOM.GROUP_CODE" +
            "      AND ID=SYS_ROLE_POPEDOM.CODE" +
            "      AND SYS_ROLE_POPEDOM.ROLE_CODE='" + roleCode + "'" +
            " ORDER BY SEQ";
        return select(sql);
    }
}
