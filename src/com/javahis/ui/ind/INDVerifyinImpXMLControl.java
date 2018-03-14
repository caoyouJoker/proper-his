package com.javahis.ui.ind;

import java.io.File;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.javahis.util.StringUtil;
import com.dongyang.ui.TTextFormat;

/**
 * <p>
 * Title: 引用订单Control
 * </p>
 *
 * <p>
 * Description: 引用订单Control
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: blurcore
 * </p>
 *
 * @author liyh 
 * @version 1.0
 */

public class INDVerifyinImpXMLControl
    extends TControl {

    // 返回数据集合
    private TParm resultParm;

    // 订购部门
    private String org_code;

    TTable tblPat;
    public INDVerifyinImpXMLControl() {
        super();
    }

    /**
     * 初始化方法
     */
    public void onInit() {
        // 初始画面数据
        initPage();
    }

    /**
     * 查询方法
     */
    public void onQuery() {
      
    }

    /**
     * 清空方法
     */
    public void onClear() {
        String clearStr =
            "PURORDER_NO;ORDER_CODE;ORDER_DESC;SELECT_ALL";
        this.clearValue(clearStr);
        getTextFormat("SUP_CODE").setText("");
        getTable("TABLE").removeRowAll();
    }

    /**
     * 传回方法
     */
    public void onReturn() {
        TTable table = getTable("TABLE");
        table.acceptText();
        if (table.getRowCount() < 0) {
            return;
        }
        TParm result = resultParm;
        int count = 0 ;
        TParm resuTParm = new TParm();
        for (int i = 0 ; i< result.getCount(); i++) {
            if ("Y".equals(table.getItemString(i, "SELECT_FLG"))) {
            	count++;
                resuTParm.addData("PATH", result.getData("PATH",i));
            }
        }
        
        if(count > 1){
        	this.messageBox("每次导入只能选择一个文件");
        	return;
        }
        
        if (result == null) {
            this.messageBox("没有传回数据");
            return;
        }
        setReturnValue(resuTParm);
//        System.out.println("--查询数据: " + resuTParm);
        this.closeWindow();
    }

    /**
     * 全选复选框选中事件
     */
    public void onCheckSelectAll() {
        TTable table = getTable("TABLE");
        table.acceptText();
        if (table.getRowCount() < 0) {
            getCheckBox("SELECT_ALL").setSelected(false);
            return;
        }
        for (int i = 0; i < table.getRowCount(); i++) {
            table.setItem(i, "SELECT_FLG", getValueString("SELECT_ALL"));
        }
    }
    
    /**
     * 表格(TABLE)复选框改变事件
     *
     * @param obj
     */
    public void onTableCheckBoxClicked(Object obj) {
    	  TTable table = getTable("TABLE");
        // 获得点击的table对象
        TTable tableDown = (TTable) obj;
        // 只有执行该方法后才可以在光标移动前接受动作效果（框架需要）
        tableDown.acceptText();
        // 获得选中的列
        int column = tableDown.getSelectedColumn();
        // 获得选中的行
        int row = tableDown.getSelectedRow();
        table.setItem(row, "SELECT_FLG", "Y");
       
    }

	/**
	 * 病患TABLE点击事件，整个TABLE只能有一个被点选
	 */
	public void onTblPatClick() {
		boolean value = TCM_Transform.getBoolean(tblPat.getValueAt(
				tblPat.getSelectedRow(), 0));
		int allRow = tblPat.getRowCount();

		for (int i = 0; i < allRow; i++) {
			tblPat.setValueAt(false, i, 0);
			tblPat.getParmValue().setData("SELECT_FLG", i, false);
		}
		tblPat.setValueAt(true, tblPat.getSelectedRow(), 0);
		tblPat.getParmValue().setData("SELECT_FLG", tblPat.getSelectedRow(), true);
		// System.out.println("click parm======"+tblPat.getParmValue());

	}

    /**
     * 接受返回值方法
     *
     * @param tag
     * @param obj
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        String order_code = parm.getValue("ORDER_CODE");
        if (!StringUtil.isNullString(order_code))
            getTextField("ORDER_CODE").setValue(order_code);
        String order_desc = parm.getValue("ORDER_DESC");
        if (!StringUtil.isNullString(order_desc))
            getTextField("ORDER_DESC").setValue(order_desc);
    }

    /**
     * 初始画面数据
     */
    private void initPage() {
    	//得到上传文件路径
    	 String path = getFilePath();
         File filePath = new File(path);
         TParm parm = new TParm();
         if(filePath.isDirectory()){
        	 String[] fileList = filePath.list();
        	 if(null != fileList && fileList.length > 0){
        		 int i = 0;
        		 for (String s : fileList) {
					String delFilePath = path + "\\/" + s;
//					System.out.println("--delFilePath: "+delFilePath);
					File delFile = new File(delFilePath);
					String absolutePath = delFile.getAbsolutePath();
					parm.addData("SELECT_FLG", "N");
					parm.addData("NAME",delFile.getName());
					parm.addData("PATH",absolutePath);
/*					System.out.println("path:"+ delFile.getPath());
					System.out.println("absolutepath:"+ delFile.getAbsolutePath());
					System.out.println("name:"+ delFile.getName());*/
					i++;
				}
        		parm.setCount(i); 
        	 }
         }
//         System.out.println("parm:"+parm);
         tblPat = getTable("TABLE");
         tblPat.setParmValue(parm);
         resultParm = parm;
    }
    
    /**
     * 得到上传文件路径
     * @return string
     * @author liyh
     * @date 20120905
     */
    private String getFilePath(){
    	//默认路径
    	String filePath = "c:/upload";
    	String sql = "SELECT GROUP_ID,CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='IND_PUR_PATH' ";
    	TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
    	if(null != parm && parm.getCount()>0){
    		filePath = parm.getValue("CHN_DESC", 0).toLowerCase();
    	}
    	return filePath;
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
