package com.javahis.ui.ins;

import com.dongyang.control.TControl;
import jdo.ins.INSOpdRuleTool;
import com.dongyang.data.TParm;
import jdo.sys.Operator;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TTable;
import com.dongyang.jdo.TDataStore;
import com.dongyang.manager.TIOM_Database;
import java.util.Vector;

/**
 *
 * <p>Title:门诊医保支付标准控制类 </p>
 *
 * <p>Description:门诊医保支付标准控制类 </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2008.11.03
 * @version JavaHis 1.0
 */
public class INSOpdRuleControl extends TControl {
    TParm data;
    int selectRow = -1;
    /**
     * 模糊查询
     */
    public class OrderList extends TLabel {
        TDataStore dataStore = TIOM_Database.getLocalTable("SYS_FEE");
        public String getTableShowValue(String s) {
            if(dataStore==null)
                return s ;
            String bufferString = dataStore.isFilter()?dataStore.FILTER:dataStore.PRIMARY;
            TParm parm = dataStore.getBuffer(bufferString);
            Vector v = (Vector)parm.getData("ORDER_CODE");
            Vector d = (Vector)parm.getData("ORDER_DESC");
            int count = v.size();
            for(int i = 0;i < count;i++)
            {
                if(s.equals(v.get(i)))
                    return "" + d.get(i);
            }
            return s;
        }
    }

    public void onInit() {
        super.onInit();
        callFunction("UI|Table|addEventListener",
                     "Table->" + TTableEvent.CLICKED, this, "onTableClicked");
        //只有text有这个方法，调用sys_fee弹出框
        callFunction("UI|ORDER_CODE|setPopupMenuParameter","ORDER_CODELIST","%ROOT%\\config\\sys\\SYSFeeLog.x");
        //接受回传值
        callFunction("UI|ORDER_CODE|addEventListener",TPopupMenuEvent.RETURN_VALUE,this,"popReturn");
        //模糊查询 -------start---------
        OrderList orderDesc = new OrderList();
        TTable table = (TTable)this.getComponent("Table");
        table.addItem("ORDER_LIST",orderDesc);
        //模糊查询 -------end---------
        onQuery();//ORDER_LIST
    }
    /**
     * 费用代码下拉列表选择
     * @param tag String
     * @param obj Object
     */
    public void popReturn(String tag,Object obj)
    {
        TParm parm=(TParm)obj;
        this.setValue("ORDER_CODE",parm.getValue("ORDER_CODE"));
        this.grabFocus("INSPAY_TYPE");
    }

    /**
     *增加对Table的监听
     * @param row int
     */
    public void onTableClicked(int row) {

        if (row < 0)
            return;
        setValueForParm("CTZ1_CODE;CTZ2_CODE;ORDER_CODE;INSPAY_TYPE;OWN_RATE;"+
                        "APPROVE_FLG;DESCRIPTION;ADDPAY_FLG;ADDPAY_RATE;ACTION_CLASS",
                        data, row);
        selectRow = row;
        //初始化Combo不可编辑
        callFunction("UI|CTZ1_CODE|setEnabled", false);
        callFunction("UI|CTZ2_CODE|setEnabled", false);
        callFunction("UI|ORDER_CODE|setEnabled", false);

    }
    /**
     * 查询
     */
    public void onQuery() {
        TParm parm = getParmForTag("CTZ1_CODE;CTZ2_CODE;ORDER_CODE", true);
        data = INSOpdRuleTool.getInstance().selectdata(parm);
        if (data.getErrCode() < 0) {
            messageBox(data.getErrText());
            return;
        }
        this.callFunction("UI|Table|setParmValue", data);
    }

    /**
     * 新增
     */
    public void onInsert() {
        //不能输入空值
        if (!this.emptyTextCheck("CTZ1_CODE,ORDER_CODE"))
            return;
        TParm parm = getParmForTag("CTZ1_CODE;CTZ2_CODE;ORDER_CODE;INSPAY_TYPE;OWN_RATE:double;"+
                                   "APPROVE_FLG;DESCRIPTION;ADDPAY_FLG;ADDPAY_RATE:double;ACTION_CLASS");
        parm.setData("OPT_USER", Operator.getID());
//        parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = INSOpdRuleTool.getInstance().insertdata(parm);

        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        //table上加入新增的数据显示
        callFunction("UI|TABLE|addRow", parm,
                     "CTZ1_CODE;CTZ2_CODE;ORDER_CODE;INSPAY_TYPE;OWN_RATE;"+
                     "APPROVE_FLG;DESCRIPTION;ADDPAY_FLG;ADDPAY_RATE;ACTION_CLASS;OPT_USER;OPT_DATE;OPT_TERM");
        // 把数据上翻到控件显示
        int row = data.insertRow();
        data.setRowData(row, parm);
        //弹出新增成功提示框
        this.messageBox("新增成功");

    }

    /**
     * 更新
     */
    public void onUpdate() {
        TParm parm = getParmForTag("CTZ1_CODE;CTZ2_CODE;ORDER_CODE", true);
        parm.setData("INSPAY_TYPE", this.getValue("INSPAY_TYPE"));
        parm.setData("OWN_RATE", TCM_Transform.getDouble(getValue("OWN_RATE")));
        parm.setData("APPROVE_FLG", this.getValue("APPROVE_FLG"));
        parm.setData("DESCRIPTION", this.getValue("DESCRIPTION"));
        parm.setData("ADDPAY_FLG", this.getValue("ADDPAY_FLG"));
        parm.setData("ADDPAY_RATE", TCM_Transform.getDouble(getValue("ADDPAY_RATE")));
        parm.setData("ACTION_CLASS", this.getValue("ACTION_CLASS"));
        parm.setData("CTZ2_CODENULL", this.getValue("CTZ2_CODE"));
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = INSOpdRuleTool.getInstance().updatedata(parm);
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        //刷新，设置末行某列的值
        int row = (Integer) callFunction("UI|Table|getSelectedRow");
        if (row < 0)
            return;
        data.setRowData(row, parm);
        callFunction("UI|Table|setRowParmValue", row, data);
        this.messageBox("更新成功");
    }

    /**
     * 保存
     */
    public void onSave() {
        if (selectRow == -1) {
            onInsert();
            return;
        }
        onUpdate();
    }


    /**
     * 删除
     */
    public void onDelete() {
        if (this.messageBox("询问", "是否删除", 2) == 0) {
            if (selectRow == -1)
                return;
            String ctz1Code = getValue("CTZ1_CODE").toString();
            String ctz2Code = getValue("CTZ2_CODE").toString();
            String orderCode = getValue("ORDER_CODE").toString();
            TParm result = INSOpdRuleTool.getInstance().deletedata(ctz1Code,
                    ctz2Code, orderCode);
            if (result.getErrCode() < 0) {
                messageBox(result.getErrText());
                return;
            }
            //删除单行显示
            int row = (Integer) callFunction("UI|Table|getSelectedRow");
            if (row < 0)
                return;
            this.callFunction("UI|Table|removeRow", row);
            this.callFunction("UI|Table|setSelectRow", row);

            this.messageBox("删除成功");
        } else {
            return;
        }
    }

    /**
     *清空
     */
    public void onClear() {
        clearValue("CTZ1_CODE;CTZ2_CODE;ORDER_CODE;INSPAY_TYPE;OWN_RATE;"+
                   "APPROVE_FLG;DESCRIPTION;ADDPAY_FLG;ADDPAY_RATE;ACTION_CLASS");
        this.callFunction("UI|Table|clearSelection");
        selectRow = -1;
        callFunction("UI|CTZ1_CODE|setEnabled", true);
        callFunction("UI|CTZ2_CODE|setEnabled", true);
        callFunction("UI|ORDER_CODE|setEnabled", true);
        callFunction("UI|ADDPAY_RATE|setEnabled", true);
        callFunction("UI|ADDPAY_FLG|setEnabled", true);
        callFunction("UI|OWN_RATE|setEnabled", true);
    }

    /**
     * 根据支付类别，选择自付比例
     */
    public void getOwnRateByInspayType() {
        if (getValue("INSPAY_TYPE").equals("A")) {
            setValue("OWN_RATE", 0);
            callFunction("UI|OWN_RATE|setEnabled", false);
        }
        if (getValue("INSPAY_TYPE").equals("B")) {
            callFunction("UI|OWN_RATE|setEnabled", true);
        }
        if (getValue("INSPAY_TYPE").equals("C")) {
            setValue("OWN_RATE", 1);
            callFunction("UI|OWN_RATE|setEnabled", false);
        }
        if (getValue("INSPAY_TYPE").equals("D")) {
            setValue("OWN_RATE", 0);
            callFunction("UI|OWN_RATE|setEnabled", true);
        }
        if (getValue("INSPAY_TYPE").equals("E")) {
            setValue("OWN_RATE", 0);
            callFunction("UI|OWN_RATE|setEnabled", true);
        }
        if (getValue("INSPAY_TYPE").equals("F")) {
            setValue("OWN_RATE", 0);
            callFunction("UI|OWN_RATE|setEnabled", true);
        }
    }
    public static void main(String[] args) {
        com.javahis.util.JavaHisDebug.initClient();
    }

}
