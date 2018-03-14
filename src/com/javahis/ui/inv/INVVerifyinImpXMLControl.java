package com.javahis.ui.inv;

import java.io.File;

import org.apache.xmlbeans.StringEnumAbstractBase.Table;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;


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
 * Copyright: Copyright (c) 2013.7.31      
 * </p>
 *
 * <p>
 * Company: blurcore
 * </p>
 *
 * @author chenx 
 * @version 4.0
 */

public class INVVerifyinImpXMLControl
    extends TControl {

    // 返回数据集合
    private TParm resultParm;

    TTable tblPat;
    public INVVerifyinImpXMLControl() {
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
        
        if (count == 0) {
            this.messageBox("没有传回数据");
            return;
        }
        setReturnValue(resuTParm);
        this.closeWindow();
    } 
    /**
     * 初始画面数据
     */
    private void initPage() {
    	//得到上传文件路径
    	 String path = getFilePath();
         File filePath = new File(path);
         TParm parm = new TParm();
         //java中的isDirectory()是检查一个对象是否是文件夹。返回值是boolean类型的。如果是则返回true，否则返回false。
         if(filePath.isDirectory()){
        	 String[] fileList = filePath.list();
        	 if(null != fileList && fileList.length > 0){
        		 int i = 0;
        		 for (String s : fileList) {
					String delFilePath = path + "\\/" + s;
					File delFile = new File(delFilePath);
					//返回抽象路径名的绝对路径名字符串
					String absolutePath = delFile.getAbsolutePath();
					parm.addData("SELECT_FLG", "N"); 
					parm.addData("NAME",delFile.getName());
					parm.addData("PATH",absolutePath);
					i++;
				}
        		parm.setCount(i); 
        	 }
         }
         tblPat = getTable("TABLE");
         tblPat.setParmValue(parm);
         resultParm = parm;
    }
    
    /**
     * 得到上传文件路径
     * @return string
     * @author 
     * @date 
     */
    private String getFilePath(){
    	//默认路径   
    	String filePath = "c:/upload";
    	return filePath;    
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
     * 表格(TABLE)复选框改变事件
     *
     * @param obj
     */
    public void onTableCheckBoxClicked(int  row) {
    	if(row<0)   
    		return ;
    	row = tblPat.getSelectedRow() ;
    	for(int i=0;i<tblPat.getRowCount();i++){
    		tblPat.setItem(i, "SELECT_FLG", "N");	
    	}
      tblPat.setItem(row, "SELECT_FLG", "Y");
       
    }
}

