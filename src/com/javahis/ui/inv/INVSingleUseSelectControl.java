package com.javahis.ui.inv;

import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTextFieldEvent;

/**
 * <p>
 * Title: 领用记录弹出
 * </p>
 *
 * <p>
 * Description: 领用记录弹出
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: JavaHis  
 * </p>
 *
 * @author lit 2013.12.16
 * @version 1.0
 */
public class INVSingleUseSelectControl
    extends TControl {

    // 返回数据集合
    private TParm resultParm;

    private TParm parm;

    public INVSingleUseSelectControl() { 
    }

    /**
     * 初始化方法
     */
    public void onInit() {         
        // 取得传入参数
        Object obj = getParameter();
        if (obj != null) {
            parm = (TParm) obj;
        } 

        // 初始画面数据
        initPage();
    }

    /**
     * 初始画面数据
     */
    private void initPage() {
        // 初始化TABLE
    	
    	TParm dParm=new TParm();
    	//SFLG;INV_CODE;INV_CHN_DESC;;DESCRIPTION;UNIT_DESC;SUP_DESC;SUP_CHN_DESC
				String sqlString="select 'N' SFLG ,f.inv_code,b.inv_chn_desc,b.DESCRIPTION,u.UNIT_CHN_DESC UNIT_DESC ,d.sup_chn_desc SUP_DESC,e.sup_chn_desc sup_chn_desc" +
				" from inv_stockm f" +
				" left join inv_base b on b.inv_code=f.inv_code " +
				" left join SYS_SUPPLIER d on D.SUP_CODE=b.sup_code " +
				" left join SYS_SUPPLIER e on e.SUP_CODE=b.up_sup_code " +
				" left join SYS_UNIT u on u.UNIT_CODE=b.stock_unit " +
				
				" where f.org_code='"+Operator.getDept()+"'";
				System.out.println(sqlString);
		 TParm parm1 = new TParm(TJDODBTool.getInstance().select(sqlString));
		 TParm mainParm=new TParm(); 
		 for (int i = 0; i < parm1.getCount("INV_CODE"); i++) {
			 mainParm.setData("SFLG",i, parm1.getValue("SFLG", i));
			 mainParm.setData("INV_CODE",i, parm1.getValue("INV_CODE", i));
			 mainParm.setData("INV_CHN_DESC",i, parm1.getValue("INV_CHN_DESC", i));
			 mainParm.setData("DESCRIPTION", i,parm1.getValue("DESCRIPTION", i));
			 mainParm.setData("UNIT_DESC", i,parm1.getValue("UNIT_DESC", i));
			 mainParm.setData("SUP_DESC",i, parm1.getValue("SUP_DESC", i));
			 mainParm.setData("SUP_CHN_DESC",i, parm1.getValue("SUP_CHN_DESC", i));
		}
		 getTable("TABLE").setParmValue(mainParm);
    }






    /**
     * 传回方法
     */
    public void onReturn() {
        TTable table = getTable("TABLE");
        TParm  parm1=  table.getParmValue();
        table.acceptText();
        TParm mainParm=new TParm();
        int m=0;
        for (int i = 0; i < table.getRowCount(); i++) {
			if ("Y".equals(table.getParmValue().getValue("SFLG", i))) {
				 mainParm.setData("SFLG",m, parm1.getValue("SFLG", i));
				 mainParm.setData("INV_CODE",m, parm1.getValue("INV_CODE", i));  
				 mainParm.setData("INV_CHN_DESC",m, parm1.getValue("INV_CHN_DESC", i));
				 mainParm.setData("DESCRIPTION", m,parm1.getValue("DESCRIPTION", i));
				 mainParm.setData("UNIT_DESC", m,parm1.getValue("UNIT_DESC", i));
				 mainParm.setData("SUP_DESC",m, parm1.getValue("SUP_DESC", i));
				 mainParm.setData("SUP_CHN_DESC",m, parm1.getValue("SUP_CHN_DESC", i));
				 m++;
			}
		}
        setReturnValue(mainParm);
        this.closeWindow();
    }

    /**
     * 得到ComboBox对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TComboBox getComboBox(String tagName) {
        return (TComboBox) getComponent(tagName);
    }

    /**
     * 得到CheckBox对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TCheckBox getCheckBox(String tagName) {
        return (TCheckBox) getComponent(tagName);
    }

    /**
     * 得到TextField对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
    }

    /**
     * 得到Table对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }

    /**
     * 得到TextFormat对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTextFormat getTextFormat(String tagName) {
        return (TTextFormat) getComponent(tagName);
    }

}
