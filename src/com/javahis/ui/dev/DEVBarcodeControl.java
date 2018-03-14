package com.javahis.ui.dev;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import java.util.Map;
import java.util.HashMap;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTableNode;

/**
 * <p>Title: 设备入库条码打印界面控制类</p>
 *
 * <p>Description: 设备入库条码打印界面控制类</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: javahis</p>
 *
 * @author sundx
 * @version 1.0
 */
public class DEVBarcodeControl extends TControl {

    /**
     * 初始化方法
     */
    public void onInit() {
        super.onInit();
        //设备明细表格编辑事件
        addEventListener("DEV_INWAREHOUSED->" + TTableEvent.CHANGE_VALUE,"onTableValueChange");
        onInitParm((TParm)getParameter());
    }

    /**
     * 通过传入数据初始化界面
     * @param parm TParm
     */
    private void onInitParm(TParm parm){
        //得到入库单明细信息
        TParm inParmD = parm.getParm("DEV_INWAREHOUSED");
        //得到入库单明细序号管理信息
        TParm inParmDD = parm.getParm("DEV_INWAREHOUSEDD");
        //创建入库单明细变量
        TParm parmD = new TParm();
        //取得入库单明细序号管理变量
        TParm parmDD = new TParm();
        //创建设备规格对照表
        Map mapSpe = new HashMap();
        //创建设备种类对照表
        Map mapKind = new HashMap();
        for(int i = 0;i<inParmD.getCount("DEV_CODE");i++){
            //如果存在序号管理则记录则不放入明细变量只记录其规格种类
            if(inParmD.getData("SEQMAN_FLG",i).equals("Y")){
                mapSpe.put(inParmD.getData("SEQ_NO",i),inParmD.getData("SPECIFICATION",i));
                mapKind.put(inParmD.getData("SEQ_NO",i),inParmD.getData("DEVKIND_CODE",i));
                continue;
            }
            //没有序号管理则将其放入明细变量
            cloneTParm(inParmD, parmD, i);
            parmD.addData("SEL_FLG","N");
            parmD.addData("PRINT_NUM","1");
        }
        //将设备序号管理信息放入序号管理变量 
        for(int i = 0;i<inParmDD.getCount("DEV_CODE");i++){
            cloneTParm(inParmDD, parmDD, i);
            parmDD.addData("SEL_FLG","N");
            //通过设备入库明细规格种类设置序号管理信息的对个种类
            parmDD.addData("SPECIFICATION",mapSpe.get(inParmDD.getData("SEQ_NO",i)));
            parmDD.addData("DEVKIND_CODE",mapKind.get(inParmDD.getData("SEQ_NO",i)));
        }
        //设置画面商=上方空间信息  
        setValue("INWAREHOUSE_NO",parm.getData("INWAREHOUSE_NO"));
        setValue("INWAREHOUSE_DATE",parm.getData("INWAREHOUSE_DATE"));
        setValue("INWAREHOUSE_DEPT",parm.getData("INWAREHOUSE_DEPT"));
        setValue("INWAREHOUSE_USER",parm.getData("INWAREHOUSE_USER"));
        ((TTable)getComponent("DEV_INWAREHOUSED")).setParmValue(parmD);
        ((TTable)getComponent("DEV_INWAREHOUSEDD")).setParmValue(parmDD);
    }


    /**
     * 入库量改变事件
     * @param obj Object
     * @return boolean 
     */
    public boolean onTableValueChange(Object obj) {
        TTableNode node = (TTableNode) obj;
        if(node.getColumn() != 7)
            return false;
        int qty = Integer.parseInt("" + ((TTable)getComponent("DEV_INWAREHOUSED")).getValueAt(node.getRow(),5));
        if(Integer.parseInt("" + node.getValue()) > qty){
            messageBox("打印数量不可大于入库数量");
            return true;
        }
        return false;
    }       
    /**
     * 设备入库明细Table全选事件
     */
    public void onSelecctAllD(){ 
        TTable table = ((TTable)getComponent("DEV_INWAREHOUSED"));
        for(int i = 0;i < table.getRowCount();i++){
            table.getParmValue().setData("SEL_FLG",i,getValue("selectAllD"));
            table.setValueAt(getValue("selectAllD"), i, 0);
        }

    }

    /**
     * 序号设备入库明细序号管理Table全选事件
     */
    public void onSelecctAllDD(){
        TTable table = ((TTable)getComponent("DEV_INWAREHOUSEDD"));
        for(int i = 0;i < table.getRowCount();i++){
            table.getParmValue().setData("SEL_FLG",i,getValue("selectAllDD"));
            table.setValueAt(getValue("selectAllDD"), i, 0);
        }
    }
    /**
     * 拷贝TParm 
     * @param from TParm
     * @param to TParm
     * @param row int
     */
    private void cloneTParm(TParm from,TParm to,int row){
    	//得到数组
        String names[] = from.getNames();
        for(int i = 0;i < names.length;i++){
        	//parm放入对象中
            Object obj = from.getData(names[i],row);
            if(obj == null)
                obj = "";
            to.addData(names[i],obj);
        }
     }

     /**
      * 条码打印事件
      */
     public void onPrint(){
         getTTable("DEV_INWAREHOUSED").acceptText();
         getTTable("DEV_INWAREHOUSEDD").acceptText();
         //取得当前选中页签
         int index = ((TTabbedPane)getComponent("TAB_PANEL")).getSelectedIndex();
         //如果是入库明细页签则根据条码打印数量打印条码
         if(index == 0){             
            TTable table = ((TTable)getComponent("DEV_INWAREHOUSED"));
            TParm parm = table.getParmValue();
            for(int i = 0;i<table.getRowCount();i++){
                if("N".equals(parm.getValue("SEL_FLG", i)))
                    continue;
                for(int j = 0; j < parm.getInt("PRINT_NUM",i);j++){
                    String desc = parm.getValue("DEV_CHN_DESC", i);
                    String specification = parm.getValue("SPECIFICATION", i);
                    String kind = parm.getValue("DEVKIND_CODE", i);
                    //条码号为设备编号
                    String code = parm.getValue("DEV_CODE", i);
                    TParm parmI = new TParm();
                    parmI.setData("DESC", "TEXT", desc);
                    parmI.setData("SPECIFICATION", "TEXT", specification);
                    parmI.setData("KIND", "TEXT", getDevKindDesc(kind));
                    parmI.setData("CODE", "TEXT", code);
                    openPrintDialog("%ROOT%\\config\\prt\\dev\\DevBarcode.jhw",
                                    parmI);
                }
            }
         }
         //如果是入库明细序号管理信息
         else{ 
             TTable table = ((TTable)getComponent("DEV_INWAREHOUSEDD"));
             TParm parm = table.getParmValue();
             for(int i = 0;i<table.getRowCount();i++){
                 if("N".equals(parm.getValue("SEL_FLG", i)))
                    continue;
                 String desc = parm.getValue("DEV_CHN_DESC",i);
                 String specification = parm.getValue("SPECIFICATION",i);
                 String kind = parm.getValue("DEVKIND_CODE",i);
                 String code = parm.getValue("DEV_CODE",i);
                 String seq = parm.getValue("DEVSEQ_NO",i);
                 seq = "0000".substring(0,4 - seq.length()) + seq;
                 TParm parmI = new TParm();
                 parmI.setData("DESC","TEXT",desc);
                 parmI.setData("SPECIFICATION","TEXT",specification);
                 parmI.setData("KIND","TEXT",getDevKindDesc(kind));
                 //条码号为设备编号加设备流水号
                 parmI.setData("CODE","TEXT",code + seq);
                 openPrintDialog("%ROOT%\\config\\prt\\dev\\DevBarcode.jhw",parmI);
            }
         }
     }

     /**
      * 得到数据库访问工具
      * @return TJDODBTool
      */
     public TJDODBTool getDBTool() {
         return TJDODBTool.getInstance();
     }

     /**
      * 取得设备分类中文描述
      * @param devKindCode String
      * @return String
      */
     public String getDevKindDesc(String devKindCode){
         TParm parm = new TParm(getDBTool().select(" SELECT CHN_DESC FROM SYS_DICTIONARY "+
                                                   " WHERE GROUP_ID = 'DEVKIND_CODE'"+
                                                   " AND   ID = '"+devKindCode+"'"));
         return parm.getValue("CHN_DESC",0);
    }
     /**
      * 拿到TTable
      * @param tag String
      * @return TTable
      */
     public TTable getTTable(String tag){
         return (TTable)getComponent(tag);
    }
}
