package jdo.sys;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.javahis.util.JavaHisDebug;
/**
 *
 * <p>Title: 用户权限工具类</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author lzk 2008.9.26
 * @version 1.0
 */
public class SYSRolePopedomTool extends TJDOTool{
    /**
     * 实例
     */
    public static SYSRolePopedomTool instanceObject;
    /**
     * 得到实例
     * @return SYSRolePopedomTool
     */
    public static SYSRolePopedomTool getInstance() {
        if (instanceObject == null)
            instanceObject = new SYSRolePopedomTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public SYSRolePopedomTool() {
        setModuleName("sys\\SYSRolePopedom.x");
        onInit();
    }
    /**
     * 得到权限
     * @param role String 角色
     * @param groupCode String 组
     * @param code String 编号
     * @return boolean true 有权限 false 没有权限
     */
    public boolean getPopedom(String role,String groupCode,String code)
    {
        TParm parm = new TParm();
        parm.setData("ROLE_CODE",role);
        parm.setData("GROUP_CODE",groupCode);
        parm.setData("CODE",code);
        return getResultInt(query("getPopedom",parm),"COUNT") > 0;
    }
    /**
     * 得到系统列表
     * @param group String
     * @param roleCode String
     * @return TParm
     */
    public TParm getSystemList(String group,String roleCode)
    {
        TParm parm = DictionaryTool.getInstance().getGroupList(group);
        int count = parm.getCount();
        for(int i = 0;i < count;i++)
        {
            String id = parm.getValue("ID",i);
//            System.out.println("-----------------"+id);
            String value = getPopedom(roleCode,group,id)?"Y":"N";
            TParm childParm = getSystemList(id,roleCode);
            parm.addData("VALUE",value);
            parm.addData("CHILD",childParm.getData());
        }
        return parm;
    }
    /**
     * 查询
     * @param roleCode String
     * @return TParm
     */
    public TParm queryRoleTree(String roleCode)
    {
        return getSystemList("SYS_SUBSYSTEM",roleCode);
    }
    /**
     * 保存
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm onSave(TParm parm,TConnection connection)
    {
        TParm result = delete(parm.getParm("DELETE"),connection);
        if(result.getErrCode() < 0)
            return result;
        result = insert(parm.getParm("INSERT"),connection);
        if(result.getErrCode() < 0)
            return result;
        return result;
    }
    /**
     * 删除
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm delete(TParm parm,TConnection connection)
    {
        TParm result = new TParm();
        int count = parm.getCount();
        for(int i = 0;i < count;i++)
        {
            TParm inParm = new TParm();
            inParm.setRowData(-1, parm, i);
            result = update("delete",inParm,connection);
            if(result.getErrCode() < 0)
                return result;
        }
        return result;
    }

    /**
     * 删除指定程序指定权限的所有信息
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm deleteRolePopedom(TParm parm, TConnection conn) {
        TParm result = new TParm();
        result = update("deleteRolePopedom", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 删除程序所有用户的权限,删除程序所有的使用用户
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm deleteUserRolePopedom(TParm parm, TConnection conn) {
        TParm result = new TParm();
        result = update("deleteUserRolePopedom", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }



    /**
     * 插入
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insert(TParm parm,TConnection connection)
    {
        TParm result = new TParm();
        int count = parm.getCount();
        for(int i = 0;i < count;i++)
        {
            TParm inParm = new TParm();
            inParm.setRowData(-1, parm, i);
            result = update("insert",inParm,connection);
            if(result.getErrCode() < 0)
                return result;
        }
        return result;
    }
    /**
     * 得到主程列表
     * @param roleCode String 角色编号
     * @return TParm ID,NAME
     */
    public TParm getStructureList(String roleCode)
    {
        TParm parm = new TParm();
        parm.setData("ROLE_CODE",roleCode);
        return query("getStructureList",parm);
    }
    /**
     * 得到主程列表根据权限
     * @param groupID String 组编号
     * @param roleCode String 权限编号
     * @return TParm
     */
    public TParm getStructureForRole(String groupID,String roleCode)
    {
        TParm parm = new TParm();
        parm.setData("GROUP_ID",groupID);
        parm.setData("ROLE_CODE",roleCode);
        return query("getStructureForRole",parm);
    }
    /**
     * 得到主程列表根据权限(Tree专用)
     * @param roleCode String 权限编号
     * @return TParm
     */
    public TParm getStructureTreeForRole(String roleCode)
    {
        return getStructureTreeForRole("SYS_SUBSYSTEM",roleCode);
    }
    /**
     * 得到主程列表根据权限(Tree专用)
     * @param groupID String 组编号
     * @param roleCode String 权限编号
     * @return TParm
     */
    public TParm getStructureTreeForRole(String groupID,String roleCode)
    {
        TParm parm = getStructureForRole(groupID,roleCode);
        int count = parm.getCount();
        for(int i = 0;i < count;i++)
        {
            String id = parm.getValue("ID",i);
            TParm childParm = getStructureTreeForRole(id,roleCode);
            parm.addData("CHILD",childParm.getData());
        }
        return parm;
    }
    public static void main(String args[])
    {
        JavaHisDebug.initClient();
        //System.out.println(SYSRolePopedomTool.getInstance().getStructureTreeForRole("ADMIN"));
        //System.out.println(SYSRolePopedomTool.getInstance().getStructureList("ADMIN"));
    }
}
