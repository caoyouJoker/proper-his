package jdo.sys;

import com.dongyang.data.TParm;
import com.dongyang.ui.TTreeNode;
import java.util.Map;
/**
 *
 * <p>Title:公用方法 </p>
 *
 * <p>Description:公用方法 </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:javahis </p>
 *
 * @author fudw
 * @version 1.0
 */
public class SYSPublic {
    /**
     * 得到节点下多少层
     * @param node TTreeNode(树的结点,一般是根节点)
     * @return TParm(返回层的方法)
     */
    public static TParm getTreeLoyar(TTreeNode node) {
        //检核入参不能为空
        if(node==null)
            return null;
        TParm parm = new TParm();
        //层号
        int loyar = 0;
        //如果存在子节点
        while (getTreeNodeChild(node)) {
            loyar++;
            //查找第一个子节点
            node = (TTreeNode) node.getChildAt(0);
            //返回数据增加一行
            int row = parm.insertRow();
            //添加ID
            parm.setData("ID", row, loyar);
            //添加name
            parm.setData("NAME", row, "第" + loyar + "层");
            //更新行号
            parm.setCount(loyar);
        }
        return parm;
    }
    /**
     * 检核节点是否还存在子节点
     * @param node TTreeNode
     * @return boolean
     */
    public static boolean getTreeNodeChild(TTreeNode node) {
        if (node.getChildCount() > 0)
            return true;
        return false;
    }
    /**
     * 得到单位表中的所有单位
     * @return Map
     */
    public static Map getUnitMap(){
        return SYSUnitTool.getInstance().getUnitMap();
    }

}
