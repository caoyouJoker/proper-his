package jdo.sys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TStrike;
import com.dongyang.ui.TTreeNode;
import com.dongyang.util.StringTool;

/**
 *
 * <p>Title: 编号规则类别工具类</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author lzk 2008.12.19
 * @version 1.0
 */
public class SYSRuleTool extends TStrike{
    /**
     * 最大级数
     */
    public static int CLASSIFY_MAX = 5;
    /**
     * 编号规则类别
     */
    private String ruleType;
    /**
     * 是否加载
     */
    private boolean isLoad;
    /**
     * 总数
     */
    private int tot;
    /**
     * 码数
     */
    private int classify[] = new int[CLASSIFY_MAX];
    /**
     * 使用级数
     */
    private int classifyCurrent = CLASSIFY_MAX;
    /**
     * 流水序号码数
     */
    private int serial;
    /**
     * 模式 A 自动编码 B 手动编码
     */
    private String mode;
    /**
     * 引用表单公用界面大分类
     */
    private static final String EXA_ROOT_SQL=
    	"SELECT CATEGORY_CODE,CATEGORY_CHN_DESC FROM SYS_CATEGORY B,SYS_RULE A " +
    		" WHERE A.RULE_TYPE=B.RULE_TYPE" +
    		" AND A.RULE_TYPE='EXM_RULE'" +
    		" AND length(B.CATEGORY_CODE)=(select A.CLASSIFY1 FROM SYS_RULE A where A.RULE_TYPE='EXM_RULE') ";
    /**
     * 引用表单公用界面中分类
     */
    private static final String EXA_MID_SQL=
    	"SELECT CATEGORY_CODE,CATEGORY_CHN_DESC FROM SYS_CATEGORY B,SYS_RULE A " +
	" WHERE A.RULE_TYPE=B.RULE_TYPE" +
	" AND A.RULE_TYPE='EXM_RULE'" +
	" AND (length(B.CATEGORY_CODE)=(select (A.CLASSIFY2+A.CLASSIFY1) FROM SYS_RULE A where A.RULE_TYPE='EXM_RULE'))" +
	" AND B.CATEGORY_CODE like ";
    /**
     * 引用表单公用界面小分类
     */
    private static final String EXA_DETAIL_SQL=
    	"SELECT CATEGORY_CODE,CATEGORY_CHN_DESC FROM SYS_CATEGORY B,SYS_RULE A " +
	" WHERE A.RULE_TYPE=B.RULE_TYPE" +
	" AND A.RULE_TYPE='EXM_RULE'" +
	" AND length(B.CATEGORY_CODE)=(select (A.CLASSIFY2+A.CLASSIFY1+A.CLASSIFY3) FROM SYS_RULE A where A.RULE_TYPE='EXM_RULE')"+
	" AND B.CATEGORY_CODE like ";
    /**
     * 构造器
     * @param ruleType String
     */
    public SYSRuleTool(String ruleType)
    {
        //设置编号规则类别
        setRuleType(ruleType);
        //加载数据
        load();
    }
    /**
     * 设置编号规则类别
     * @param ruleType String
     */
    public void setRuleType(String ruleType)
    {
        this.ruleType = ruleType;
    }
    /**
     * 得到编号规则类别
     * @return String
     */
    public String getRuleType()
    {
        return ruleType;
    }
    /**
     * 加载数据
     */
    public void load()
    {
        isLoad = false;
        TDataStore dataStore = new TDataStore();
        if(!dataStore.setSQL("SELECT * FROM SYS_RULE WHERE RULE_TYPE = '" + getRuleType() + "'"))
        {
            err(dataStore.getErrText());
            return;
        }
        if(dataStore.retrieve() == -1)
        {
            err(dataStore.getErrText());
            return;
        }
        if(dataStore.rowCount() != 1)
        {
            err("没有找到" + getRuleType() + "编号规则类别");
            return;
        }
        //设置总码数
        tot = dataStore.getItemInt(0,"TOT_NUMBER");
        //设置流水序号码数
        serial = dataStore.getItemInt(0,"SERIAL_NUMBER");
        //设置编码方式
        mode = dataStore.getItemString(0,"CODE_MODE");
        //设置各级码数
        for(int i = 0;i < CLASSIFY_MAX;i++)
            classify[i] = dataStore.getItemInt(0,"CLASSIFY" + (i + 1));
        //计算有效级数
        classifyCurrent = CLASSIFY_MAX;
        for(int i = 0;i < CLASSIFY_MAX;i++)
            if(classify[i] == 0)
            {
                classifyCurrent = i;
                break;
            }
        isLoad = true;
    }
    /**
     * 是否已经加载
     * @return boolean true 已经加载数据 false 没有加载数据
     */
    public boolean isLoad()
    {
        return isLoad;
    }
    /**
     * 得到总码数
     * @return int
     */
    public int getTot()
    {
        return tot;
    }
    /**
     * 得到某层码数
     * @param i int
     * @return int
     */
    public int getClassify(int i)
    {
        if(i < 0 || i >= CLASSIFY_MAX)
            return -1;
        return classify[i];
    }
    /**
     * 得到有效级数
     * @return int
     */
    public int getClassifyCurrent()
    {
        return classifyCurrent;
    }
    /**
     * 得到流水序号码数
     * @return int
     */
    public int getSerial()
    {
        return serial;
    }
    /**
     * 得到编码方式
     * @return String A 自动编码 B 手动编码
     */
    public String getMode()
    {
        return mode;
    }
    /**
     * 得到号码层数
     * @param s String
     * @return int
     */
    public int getNumberClass(String s)
    {
        int length = s.length();
        for(int i = 0;i < getClassifyCurrent();i++)
        {
            length -= getClassify(i);
            if(length == 0)
                return i + 1;
        }
        if(length == getSerial())
            return getClassifyCurrent() + 1;
        return -1;
    }
    /**
     * 得到层编码个数
     * @param classifyIndex int
     * @return int
     */
    public int getClassNumber(int classifyIndex)
    {
        if(classifyIndex <= 0 || classifyIndex > getClassifyCurrent() + 1)
            return -1;
        if(classifyIndex == getClassifyCurrent() + 1)
            return getTot();
        int c = 0;
        for(int i = 0;i < classifyIndex;i++)
            c += getClassify(i);
        return c;
    }
    /**
     * 得到上层编码
     * @param s String
     * @return String
     */
    public String getNumberParent(String s)
    {
        if(s == null || s.length() == 0)
            return "";
        int classifyIndex = getNumberClass(s) - 1;
        if(classifyIndex <= 0)
            return "";
        int length = getClassNumber(classifyIndex);
        return s.substring(0,length);
    }
    /**
     * 分解编号为数组
     * @param s String
     * @return String[]
     */
    public String[] getNumberArray(String s)
    {
        ArrayList list = new ArrayList();
        if(s == null || s.length() == 0)
            return new String[]{};
        int classifyIndex = getNumberClass(s);
        if(classifyIndex == -1)
            return new String[]{};
        boolean hasSerial = false;
        if(classifyIndex == getClassifyCurrent() + 1)
        {
            classifyIndex--;
            hasSerial = true;
        }
        for(int i = 0;i < classifyIndex;i++)
        {
            list.add(s.substring(0,getClassify(i)));
            s = s.substring(getClassify(i));
        }
        if(hasSerial)
            list.add(s);
        return (String[])list.toArray(new String[]{});
    }
    public TTreeNode[] getTreeNode(TDataStore dataStore,String id,String name,String type,String seq)
    {
        if(dataStore == null)
            return null;
        dataStore.setSort(id);
        dataStore.sort();
        int count = dataStore.rowCount();
        Map map = new HashMap();
        ArrayList root = new ArrayList();
        for(int i = 0;i < count;i ++)
        {
            String code = dataStore.getItemString(i,id);
            String text = dataStore.getItemString(i,name);
            int index = dataStore.getItemInt(i,seq);
            TTreeNode node = new TTreeNode(text,type);
            node.setID(code);
            node.setSeq(index);
            int classifyIndex = getNumberClass(code);
            if(classifyIndex == 1)
            {
                root.add(node);
                map.put(code,node);
                continue;
            }
            String parentCode = getNumberParent(code);
            TTreeNode parentNode = (TTreeNode)map.get(parentCode);
            if(parentNode == null)
            {
                root.add(node);
                map.put(code,node);
                continue;
            }
            parentNode.addSeq(node);
            map.put(code,node);
        }
        return (TTreeNode[])root.toArray(new TTreeNode[]{});
    }
    public TTreeNode[] getTreeNode(TDataStore dataStore,String id,String name,String type,String seq,String engName)
    {
        if(dataStore == null)
            return null;
        dataStore.setSort(id);
        dataStore.sort();
        int count = dataStore.rowCount();
        Map map = new HashMap();
        ArrayList root = new ArrayList();
        for(int i = 0;i < count;i ++)
        {
            String code = dataStore.getItemString(i,id);
            String text = dataStore.getItemString(i,name);
            String enName = dataStore.getItemString(i,engName);
            int index = dataStore.getItemInt(i,seq);
            TTreeNode node = new TTreeNode(text,type);
            node.setID(code);
            node.setEnText(enName);
            node.setSeq(index);
            int classifyIndex = getNumberClass(code);
            if(classifyIndex == 1)
            {
                root.add(node);
                map.put(code,node);
                continue;
            }
            String parentCode = getNumberParent(code);
            TTreeNode parentNode = (TTreeNode)map.get(parentCode);
            if(parentNode == null)
            {
                root.add(node);
                map.put(code,node);
                continue;
            }
            parentNode.addSeq(node);
            map.put(code,node);
        }
        return (TTreeNode[])root.toArray(new TTreeNode[]{});
    }

    /**
     * 得到新的号码
     * @param oldCode String 旧号码
     * @param classifyIndex int 层数
     * @return String
     */
    public String getNewCode(String oldCode, int classifyIndex) {
        int length = classifyIndex <= getClassifyCurrent() ?
            getClassify(classifyIndex - 1) : getSerial();
        if (oldCode == null || oldCode.length() == 0)
            oldCode = StringTool.fill("0", length);
        if (classifyIndex > 1)
            oldCode = oldCode.substring(oldCode.length() - length);
        Integer newCode = (Integer) (Integer.parseInt(oldCode) + 1);
        String code = newCode.toString();
        for (int i = 0; i < oldCode.length() - newCode.toString().length(); i++) {
            code = "0" + code;
        }
        return code;
    }

    /**
     * 得到新的号码(可以为字母)
     * @param oldCode String 旧号码
     * @param classifyIndex int 层数
     * @return String
     */
    public String getNewCodeByString(String oldCode, int classifyIndex) {
        int length = classifyIndex <= getClassifyCurrent() ?
            getClassify(classifyIndex - 1) : getSerial();
        if (oldCode == null || oldCode.length() == 0)
            oldCode = StringTool.fill("0", length);
        if (classifyIndex > 1)
            oldCode = oldCode.substring(oldCode.length() - length);
        return StringTool.stringADD(oldCode);
    }

    /**
     * 引用表单大分类
     * @return TParm
     */
    public static TParm getExaRoot(){
    	TParm result=new TParm(TJDODBTool.getInstance().select(EXA_ROOT_SQL));
    	return result;
    }
    /**
     * 引用表单中分类
     * @param t0 String 大分类的code
     * @return
     */
    public static TParm getExaMid(String t0){
    	String sql=EXA_MID_SQL+"'"+t0+"%'";
    	TParm result=new TParm(TJDODBTool.getInstance().select(sql));
    	return result;
    }
    /**
     * 引用表单中分类
     * @param t0 String 大分类的code
     * @return
     */
    public static TParm getExaDetail(String t0){
    	String sql=EXA_DETAIL_SQL+"'"+t0+"%'";
    	TParm result=new TParm(TJDODBTool.getInstance().select(sql));
    	return result;
    }
    /**
     * 显示内存(调试用)
     */
    public void showDebug()
    {
        //System.out.println("isLoad=" + isLoad());
        //System.out.println("tot=" + tot);
        //System.out.println("classifyCurrent=" + classifyCurrent);
        //System.out.println("classify=" + StringTool.getString(classify));
        //System.out.println("serial=" + serial);
        //System.out.println("mode=" + mode);
    }

    public static void main(String args[])
    {
        com.dongyang.util.TDebug.initClient();
        SYSRuleTool tool = new SYSRuleTool("SYS_DEPT");
        tool.showDebug();
        //System.out.println(tool.getNumberClass("A0102"));
        //System.out.println(tool.getClassNumber(2));
        //System.out.println(tool.getNumberParent("A0102004"));
        //System.out.println(StringTool.getString(tool.getNumberArray("A0102004")));

        //System.out.println(tool.getNewCode("A01",2));


    }
}
