package jdo.sys;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.util.TDebug;
import java.util.ArrayList;
import com.dongyang.util.StringTool;
import jdo.reg.SessionTool;

public class DictionaryTool extends TJDOTool{
    /**
     * 实例
     */
    public static DictionaryTool instanceObject;
    /**
     * 得到实例
     * @return DictionaryTool
     */
    public static DictionaryTool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new DictionaryTool();
        return instanceObject;
    }
    /**
     * 构造器
     */
    public DictionaryTool()
    {
        setModuleName("sys\\SYSDictionaryModule.x");
        onInit();
    }
    /**
     * 得到名称
     * @param groupID String 组编号
     * @param id String 编号
     * @return String 名称
     */
    public String getName(String groupID,String id)
    {
        TParm parm = new TParm();
        parm.setData("GROUP_ID",groupID);
        parm.setData("ID",id);
        return getResultString(query("getName",parm),"NAME");
    }
    /**
     * 得到名称
     * @param groupID String 组编号
     * @param id String 编号
     * @return String 名称
     */
    public String getEnName(String groupID,String id)
    {
        TParm parm = new TParm();
        parm.setData("GROUP_ID",groupID);
        parm.setData("ID",id);
        return getResultString(query("getEnName",parm),"NAME");
    }
    /**
     * 得到组列表
     * @return TParm 回值<ID>,<NAME>,<TYPE>
     */
    public TParm getGroupList()
    {
        return getGroupList("GROUP_ID");
    }
    /**
     * 得到组列表
     * @param group String 组名
     * @return TParm 回值<ID>,<NAME>,<TYPE>
     */
    public TParm getGroupList(String group)
    {
        TParm parm = new TParm();
        parm.setData("GROUP_ID",group);
        return query("getGroupList",parm);
    }
    /**
     * 得到组名称
     * @param id String 编号
     * @return String 组名
     */
    public String getGroupName(String id)
    {
        TParm parm = new TParm();
        parm.setData("ID",id);
        return getResultString(query("getGroupName",parm),"NAME");
    }
    /**
     * 列表整组数据
     * @param groupID String 组编号
     * @return TParm 回值
     */
    public TParm getListAll(String groupID)
    {
        TParm parm = new TParm();
        parm.setData("GROUP_ID",groupID);
        return query("getListAll",parm);
    }
    public boolean insert(String groupID,String id,String name,int seq)
    {
        return insert(groupID,id,name,"1","","","","","","",seq);
    }
    /**
     * 查询
     * @param groupID String
     * @param id String
     * @return TParm
     */
    public TParm query(String groupID,String id)
    {
        TParm parm = new TParm();
        parm.setData("GROUP_ID",groupID);
        parm.setData("ID",id);
        return query("query",parm);
    }
    /**
     * 插入
     * @param groupID String
     * @param id String
     * @param name String
     * @param type String
     * @param parentID String
     * @param state String
     * @param data String
     * @param description String
     * @param optID String
     * @param optIP String
     * @param seq int
     * @return boolean
     */
    public boolean insert(String groupID,String id,String name,String type,
                          String parentID,String state,String data,
                          String description,String optID,String optIP,int seq)
    {
        TParm parm = new TParm();
        parm.setData("GROUP_ID",groupID);
        parm.setData("ID",id);
        parm.setData("NAME",name);
        parm.setData("TYPE",type);
        parm.setData("PARENT_ID",parentID);
        parm.setData("STATE",state);
        parm.setData("DATA",data);
        parm.setData("DESCRIPTION",description);
        parm.setData("OPT_ID",optID);
        parm.setData("OPT_IP",optIP);
        parm.setData("SEQ",seq);
        TParm result = update("insert",parm);
        if(result.getErrCode() != 0)
        {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return false;
        }
        return true;
    }
    /**
     * 删除
     * @param groupID String 组编号
     * @param id String 编号
     * @return boolean true 成功 false 失败
     */
    public boolean delete(String groupID,String id)
    {
        TParm parm = new TParm();
        parm.setData("GROUP_ID",groupID);
        parm.setData("ID",id);
        TParm result = update("delete",parm);
        if(result.getErrCode() != 0)
        {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return false;
        }
        return true;
    }
    /**
     * 得到姓名中文
     * @param sexCode String 性别编号
     * @return String
     */
    public String getSexName(String sexCode)
    {
        return getName("SYS_SEX",sexCode);
    }
    /**
     * 得到门急编号
     * @return String[]
     */
    public String[] getADMOEType()
    {
        TParm parm = getGroupList("SYS_ADMoeTYPE");
        if(parm.getErrCode() < 0)
            return null;
        ArrayList list = new ArrayList();
        int count = parm.getCount();
        for(int i = 0;i < count;i ++)
            list.add(parm.getValue("ID",i));
        return (String[])list.toArray(new String[]{});
    }
    /**
     * 得到全部门急数段
     * @return String[]
     */
    public String[] getSessionCode()
    {
        String s[] = getADMOEType();
        ArrayList list = new ArrayList();
        if(s == null)
            return null;
        for(int i = 0;i < s.length;i++)
        {
            String session[] = SessionTool.getInstance().getSessionCode(s[i],"");
            for(int j = 0;j < session.length;j++)
                list.add(s[i] + ":" + session[j]);
        }
        return (String[])list.toArray(new String[]{});
    }
    /**
     * 得到收费名称
     * @return TParm
     */
    public TParm getSysCharge(String groupId){
        TParm result=new TParm();
        if(groupId==null||groupId.length()==0){
            out("入参错误");
            return result.newErrParm( -1, "组名不能为空");
        }
        result.setData("GROUP_ID",groupId);
        result=query("getSysCharge",result);
        if(result.getErrCode()<0)
            out("查询错误");
        return result;
    }
    public static void main(String args[])
    {
        //Log.DEBUG=true;
        //TDebug.initClient();
        TDebug.initServer();
        DictionaryTool tool = new DictionaryTool();
        //System.out.println(StringTool.getString(tool.getADMOEType()));
        //System.out.println(StringTool.getString(tool.getSessionCode()));
        //System.out.println(tool.getGroupName("SEX"));
        //System.out.println(tool.getName("ROOT","GROUP"));
        /*for(int i = 0;i < 10;i++)
        {
            System.out.println("i=" + i);
            System.out.println(tool.getGroupList());
        }*/
        //System.out.println(tool.getListAll("SYS_DICTIONARY"));
        //System.out.println(tool.insert("aaa","bbb","ccc",10));
        //System.out.println(tool.delete("aaa","bbb"));
        //System.out.println(tool.getName("SEX","2"));
    }
}
