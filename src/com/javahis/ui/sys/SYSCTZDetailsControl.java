package com.javahis.ui.sys;

import com.dongyang.control.*;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.data.TParm;
import jdo.sys.StatusDetailsTool;
import com.dongyang.ui.TTreeNode;
import jdo.sys.CTZTool;
import com.dongyang.ui.event.TTreeEvent;
import jdo.sys.Operator;
import jdo.sys.FeeCodeOperateTool;
import com.dongyang.ui.TTableNode;
import jdo.sys.ChargeDetailList;

/**
 * <p>Title:身份明细 </p>
 *
 * <p>Description:身份明细 </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author TParm
 * @version 1.0
 */
public class SYSCTZDetailsControl
    extends TControl {
    int selectrow = -1; //table被选择
    private static final String TREE = "Tree";

    /**
     * 初始化界面
     *  @return TParm
     */
    public void onInit() {
        super.onInit();
        //table的侦听事件
        callFunction("UI|TABLE|addEventListener",
                     "TABLE->" + TTableEvent.CLICKED, this, "onTableClicked");
        //table的值改变事件
        this.addEventListener("TABLE->" + TTableEvent.CHANGE_VALUE,
                              "onTableChangeValue");
        callFunction("UI|save|setEnabled", false);
        callFunction("UI|CTZ_CODE|setEnabled", false);
    }

    /**
     * 查询数据
     * @return TParm
     */
    public void onQuery() {
        this.callFunction("UI|Table|removeRowAll"); //清空table
       TParm data = StatusDetailsTool.getInstance().selectalldata();
        this.callFunction("UI|TABLE|setParmValue", data); //给table配参
        this.onClear(); //调用清空方法
    }
    /**
     *清空
     */
    public void onClear() {
        //清空所有数据
        clearValue("CHARGE_HOSP_CODE;OWN_RATE;CTZ_CODE;SEQ;DESCRITPION");
        callFunction("UI|TABLE|learSelectc", true);
        selectrow = -1;
        //删除不能被编辑
        callFunction("UI|save|setEnabled", false);
        callFunction("UI|TABLE|clearSelection");
    }

    /**
     *增加对Table的选中监听
     * @param row int
     */
    public void onTableClicked(int row) {
        //table接收所有事件
        this.callFunction("UI|TABLE|acceptText");
        setValueForParm("CHARGE_HOSP_CODE;CTZ_CODE;SEQ;DESCRITPION;OWN_RATE",
                        (TParm) callFunction("UI|TABLE|getParmValue"), row); //数据上翻
        selectrow = row;
        callFunction("UI|save|setEnabled", true);
    }

    /**
     *增加对Table值改变的监听
     * @param row int
     */
    public void onTableChangeValue(Object obj) {
        TTableNode node = (TTableNode) obj;
        if (node == null)
            return;
        if (node.getColumn() != 0)
            return;
        TParm data=(TParm) callFunction("UI|TABLE|getParmValue");
//        System.out.println(node.getTable().getParmValue());
        //。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。
        ChargeDetailList list = new ChargeDetailList();
        list.initParm(data);
//        for (int i = 0; i < list.size(); i++)
//            System.out.println(list.getChargeDetail(i));
//        //list.removeData(1);
//        list.getChargeDetail(0).modifySeq(1000);
//        list.getChargeDetail(0).modifyChargeHospCode("88");
//        //list.getChargeDetail(0).modifySeq(100);
//        list.newChargeDetail().setCtzCode("1");
//        list.newChargeDetail().setCtzCode("2");
//        list.newChargeDetail().setCtzCode("3");
//        System.out.println("--------------------");
//        for (int i = 0; i < list.size(); i++)
//            System.out.println(list.getChargeDetail(i));
//        System.out.println(list.getParm());

    }
    /**
     * 保存数据
     */
    public void onSave() {
        this.callFunction("UI|TABLE|acceptText");
        //更新细表
        if (selectrow >0) {
            //更新细表配参
            TParm parm = getParmForTag(
                "CTZ_CODE;CHARGE_HOSP_CODE;OWN_RATE;CTZ_CODE;SEQ;DESCRITPION");
            parm.setData("OPT_USER", Operator.getID());
            parm.setData("OPT_TERM", Operator.getIP());
           TParm result= StatusDetailsTool.getInstance().updateData(parm);
           if (result.getErrCode() < 0) {
               this.messageBox("E0005"); //执行失败
               return;
           }
           else {
               this.messageBox("P0005"); //执行成功
           }
           TParm data=(TParm) callFunction("UI|TABLE|getParmValue");
            data.setRowData(selectrow, parm);
            callFunction("UI|TABLE|setRowParmValue", selectrow, data);
        }
        selectrow = -1;
    }

}
