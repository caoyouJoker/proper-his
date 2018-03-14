package com.javahis.ui.dev;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import jdo.dev.MainStockRoomTool;
import com.dongyang.data.TParm;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import com.dongyang.util.TMessage;

/**
 * <p>Title: 设备主库设定</p>
 *
 * <p>Description: 设备主库设定</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: javahis</p>
 *
 * @author sundx
 * @version 1.0
 */
public class MainStockRoomControl extends TControl {
    /**
     * 初始化方法
     */
    public void onInit() {
        super.onInit();
        loadTableData();
    }
    /**
     * 构造函数
     */
    public MainStockRoomControl() {
    }
    /**
     * 初始化时装在表格数据
     */
    public void loadTableData(){
        ((TTable)getComponent("TABLE")).setSQL(" SELECT DEPT_CODE,DEPT_DESC,PY1,PY2,SEQ,"+
                                               "        DEPT_DESCRIBE,REGION_CODE,MEDDEV_FLG,"+
                                               "        INFDEV_FLG,OTHERDEV_FLG,"+
                                               "        OPT_USER,OPT_DATE,OPT_TERM"+
                                               " FROM   DEV_ORG"+
                                               " ORDER BY DEPT_CODE");
        ((TTable)getComponent("TABLE")).retrieve();
    }
    /**
     * 查询方法
     */
    public void onQuery(){
        if(getValueString("DEPT_CODE").length() == 0){
            loadTableData();
            return;
        }
        ((TTable)getComponent("TABLE")).setSQL(" SELECT DEPT_CODE,DEPT_DESC,PY1,PY2,SEQ,"+
                                               "        DEPT_DESCRIBE,REGION_CODE,MEDDEV_FLG,"+
                                               "        INFDEV_FLG,OTHERDEV_FLG,"+
                                               "        OPT_USER,OPT_DATE,OPT_TERM"+
                                               " FROM   DEV_ORG"+
                                               " WHERE  DEPT_CODE = '"+getValueString("DEPT_CODE")+"'"+
                                               " ORDER BY DEPT_CODE");
        ((TTable)getComponent("TABLE")).retrieve();
        if(((TTable)getComponent("TABLE")).getRowCount() <= 0){
            setValue("DEPT_CODE","");
            setValue("DEPT_DESC","");
            setValue("PY1","");
            setValue("PY2","");
            setValue("DEPT_DESCRIBE","");
            setValue("MEDDEV_FLG","N");
            setValue("INFDEV_FLG","N");
            setValue("OTHERDEV_FLG","N");
            setValue("SEQ","");
            messageBox("查无资料");
            return;
        }
        ((TTable)getComponent("TABLE")).setSelectedRow(0);
        onTableClicked();
        ((TTextField)getComponent("DEPT_DESC")).grabFocus();
    }

    /**
     * 科室名称回车事件
     */
    public void onDeptDesc(){
        setValue("PY1",TMessage.getPy(getValueString("DEPT_DESC")));
        setValue("PY2",TMessage.getPy(getValueString("DEPT_DESC")));
        setValue("SEQ",MainStockRoomTool.getInstance().getDevDeptMaxSeq());
        ((TTextField)getComponent("DEPT_DESCRIBE")).grabFocus();
     }


    /**
     * 表格单击事件
     */
    public void onTableClicked(){
        int row  = ((TTable)getComponent("TABLE")).getSelectedRow();
        setValue("DEPT_CODE",((TTable)getComponent("TABLE")).getValueAt(row,0));
        setValue("DEPT_DESC",((TTable)getComponent("TABLE")).getValueAt(row,1));
        setValue("PY1",((TTable)getComponent("TABLE")).getValueAt(row,2));
        setValue("PY2",((TTable)getComponent("TABLE")).getValueAt(row,3));
        setValue("DEPT_DESCRIBE",((TTable)getComponent("TABLE")).getValueAt(row,5));
        setValue("SEQ",((TTable)getComponent("TABLE")).getValueAt(row,4));
        setValue("MEDDEV_FLG",((TTable)getComponent("TABLE")).getValueAt(row,6));
        setValue("INFDEV_FLG",((TTable)getComponent("TABLE")).getValueAt(row,7));
        setValue("OTHERDEV_FLG",((TTable)getComponent("TABLE")).getValueAt(row,8));
    }

    /**
     * 保存方法
     */
    public void onSave(){
        if(getValueString("DEPT_CODE").length() == 0){
            messageBox("科室代码不可为空");
            return;
        }
        TParm parm = MainStockRoomTool.getInstance().selectDevDeptInf(getValueString("DEPT_CODE"));
        if(parm.getErrCode()<0)
            return;
        TParm tParm = new TParm();
        tParm.setData("DEPT_CODE",getValueString("DEPT_CODE"));
        tParm.setData("DEPT_DESC",getValueString("DEPT_DESC"));
        tParm.setData("PY1",getValueString("PY1"));
        tParm.setData("PY2",getValueString("PY2"));
        tParm.setData("SEQ",getValueString("SEQ"));
        tParm.setData("REGION_CODE",Operator.getRegion());
        tParm.setData("DEPT_DESCRIBE",getValueString("DEPT_DESCRIBE"));
        tParm.setData("MEDDEV_FLG",getValueString("MEDDEV_FLG"));
        tParm.setData("INFDEV_FLG",getValueString("INFDEV_FLG"));
        tParm.setData("OTHERDEV_FLG",getValueString("OTHERDEV_FLG"));
        tParm.setData("OPT_USER",Operator.getID());
        tParm.setData("OPT_DATE",SystemTool.getInstance().getDate());
        tParm.setData("OPT_TERM",Operator.getIP());
        if(parm.getCount() <= 0)
            tParm = MainStockRoomTool.getInstance().insertDevDeptInf(tParm);
        else if(parm.getCount() > 0)
            tParm = MainStockRoomTool.getInstance().updateDevDeptInf(tParm);
        if(tParm.getErrCode() < 0){
            messageBox("保存失败");
            return;
        }
        messageBox("保存成功");
        loadTableData();
    }

    /**
     * 删除方法
     */
    public void onDelete(){
        if(getValueString("DEPT_CODE").length() == 0)
            return;
        TParm parm = MainStockRoomTool.getInstance().deleteDevDeptInf(getValueString("DEPT_CODE"));
        if(parm.getErrCode() < 0){
            messageBox("删除失败");
            return;
        }
        messageBox("删除成功");
        onClear();
        loadTableData();
    }
    /**
     * 清空方法
     */
    public void onClear(){
        setValue("DEPT_CODE","");
        setValue("DEPT_DESC","");
        setValue("DEPT_DESCRIBE","");
        setValue("PY1","");
        setValue("PY2","");
        setValue("MEDDEV_FLG","N");
        setValue("INFDEV_FLG","N");
        setValue("OTHERDEV_FLG","N");
        setValue("SEQ","");
        ((TTable)getComponent("TABLE")).removeRowAll();
    }

}
