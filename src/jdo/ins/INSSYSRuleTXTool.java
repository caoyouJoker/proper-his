package jdo.ins;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TStrike;
import com.dongyang.ui.TTreeNode;
/**
 * <p>
 * Title: 门诊医保三目字典
 * </p>
 * 
 * <p>
 * Description:门诊医保三目字典编号规则类别工具类
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * 
 * <p>
 * Company: BlueCore
 * </p>
 * 
 * @author pangben 2011-12-09
 * @version 2.0
 */
public class INSSYSRuleTXTool extends TStrike{
	/**
	 * 使用级数
	 */
	private int classifyCurrent = CLASSIFY_MAX;
	/**
	 * 最大级数
	 */
	private static int CLASSIFY_MAX = 5;

	/**
	 * 码数
	 */
	private int classify[] = new int[CLASSIFY_MAX];
	int serial;// 设置各级码数

	int tot;// 设置总码数

	/**
	 * 得到总码数
	 * 
	 * @return int
	 */
	public int getTot() {
		return tot;
	}

	/**
	 * 得到流水序号码数
	 * 
	 * @return int
	 */
	public int getSerial() {
		return serial;
	}
    /**
     * 是否加载
     */
    private boolean isLoad;
    /**
     * 编号规则类别
     */
    private String ruleType;
    /**
     * 构造器
     * @param ruleType String
     */
    public INSSYSRuleTXTool(String ruleType)
    {
        //设置编号规则类别
        setRuleType(ruleType);
        //加载数据
        load();
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
	 * 得到有效级数
	 * 
	 * @return int
	 */
	public int getClassifyCurrent() {
		return classifyCurrent;
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
	 * 查找目前正在启用的项目列表
	 * 
	 * @return String
	 */
	public TTreeNode[] getTreeNode(TDataStore dataStore,TParm sysFeeParm, String id,
			String name, String type, String seq) {
		if (dataStore == null)
			return null;
		dataStore.setSort(id);
		dataStore.sort();
		int count = dataStore.rowCount();
		Map map = new HashMap();
		ArrayList root = new ArrayList();
		for (int i = 0; i < count; i++) {
			String code = dataStore.getItemString(i, id);// 获得分类号码
			String text = dataStore.getItemString(i, name);// 获得分类名称
			int index = dataStore.getItemInt(i, seq);
			TTreeNode node = new TTreeNode(text, "Root");// 获得一个节点
			node.setID(code);
			node.setSeq(index);
			int classifyIndex = getNumberClass(code);
			if (classifyIndex == 1) {
				root.add(node);
				map.put(code, node);
				continue;
			}
			String parentCode = getNumberParent(code);// 得到当前节点的ID号码
			TTreeNode parentNode = (TTreeNode) map.get(parentCode);// 查看此节点是否存在
			if (parentNode == null) {
				root.add(node);
				map.put(code, node);
				continue;
			}
			parentNode.addSeq(node);// 存在节点将此节点放到父节点中
			map.put(code, node);// 累计此节点数据
			if (classifyIndex == classifyCurrent) {//最小节点
				for (int j = 0; j < sysFeeParm.getCount(); j++) {// 便利医嘱
					int indexSql = 1;
					if (sysFeeParm.getValue("ORDER_CODE", j).contains(code)) {// 查询当前医嘱号码是否包括此节点CODE值
						TTreeNode nodeTemp = new TTreeNode(sysFeeParm.getValue(
								"ORDER_DESC", j),type);// 创建子节点
						nodeTemp.setID(sysFeeParm.getValue("ORDER_CODE", j));// 子节点ID
						nodeTemp.setSeq(indexSql);// 设置此节点的顺序号
						// nodeTemp.setSeq(index);
						indexSql++;
						node.addSeq(nodeTemp);// 添加子节点
					}
				}
			}
		}
		return (TTreeNode[]) root.toArray(new TTreeNode[] {});
	}

	/**
	 * 得到上层编码
	 * 
	 * @param s
	 *            String
	 * @return String
	 */
	public String getNumberParent(String s) {
		if (s == null || s.length() == 0)
			return "";
		int classifyIndex = getNumberClass(s) - 1;
		if (classifyIndex <= 0)
			return "";
		int length = getClassNumber(classifyIndex);
		return s.substring(0, length);
	}

	/**
	 * 得到号码层数
	 * 
	 * @param s
	 *            String
	 * @return int
	 */
	public int getNumberClass(String s) {
		int length = s.length();
		for (int i = 0; i < getClassifyCurrent(); i++) {
			length -= getClassify(i);
			if (length == 0)
				return i + 1;
		}
		if (length == serial)
			return getClassifyCurrent() + 1;
		return -1;
	}

	/**
	 * 得到某层码数
	 * 
	 * @param i
	 *            int
	 * @return int
	 */
	public int getClassify(int i) {
		if (i < 0 || i >= CLASSIFY_MAX)
			return -1;
		return classify[i];
	}

	/**
	 * 得到层编码个数
	 * 
	 * @param classifyIndex
	 *            int
	 * @return int
	 */
	public int getClassNumber(int classifyIndex) {
		if (classifyIndex <= 0 || classifyIndex > getClassifyCurrent() + 1)
			return -1;
		if (classifyIndex == getClassifyCurrent() + 1)
			return getTot();
		int c = 0;
		for (int i = 0; i < classifyIndex; i++)
			c += getClassify(i);
		return c;
	}
}
