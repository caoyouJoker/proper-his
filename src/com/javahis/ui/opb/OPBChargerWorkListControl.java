package com.javahis.ui.opb;

import com.dongyang.control.*;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTableNode;
import com.dongyang.data.TParm;

public class OPBChargerWorkListControl
    extends TControl {
    int selectrow = -1;
    TParm data = new TParm();
    /**
     * 初始化界面
     */
    public void onInit() {
        super.onInit();
        //table1的侦听事件
        callFunction("UI|TABLE|addEventListener",
                     "TABLE->" + TTableEvent.CLICKED, this, "onTableClicked");
        //table1值改变事件
        this.addEventListener("TABLE->" + TTableEvent.CHANGE_VALUE,
                              "onTableChangeValue");
        callFunction("UI|TABLE|addRow");
    }

    /**
     *增加对Table的监听
     * @param row int
     */
    public void onTableClicked(int row) {
        //接收所有事件
        this.callFunction("UI|TABLE|acceptText");
        TParm data = (TParm) callFunction("UI|TABLE|getParmValue");
        setValueForParm("ACCOUNT_TYPE;ACCOUNT_USER;TOT_AMT",
                        data, row); //数据上翻
        selectrow = row;
        callFunction("UI|delete|setEnabled", true);
    }
    /**
     * 增加对Table值改变的监听
     * @param obj Object
     */
    public void onTableChangeValue(Object obj) {
        TTableNode node = (TTableNode) obj;
        if (node == null)
            return;
        if (node.getColumn() != 0)
            return;
        node.getTable().getParmValue().setData("FLG", node.getRow(),
                                               node.getValue());
    }
    /**
     * 计价
     */
//     public void onCharges(){
//
//}
    /**
     * 打印
     */
//     public void onFill(){
//
//     }
    /**
     *预览
     */
//     public void onFill(){
//
//     }
    /**
     * 汇出
     */
//     public void onFill(){
//
//     }
    /**
     * 明细
     */
//   public void onDetial(){
//       this.openDialog("%ROOT%\\config\\opb\\OPBFillDetial.x");
//   }


}
