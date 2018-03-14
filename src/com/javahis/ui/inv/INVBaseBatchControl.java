package com.javahis.ui.inv;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;

import javax.swing.JFileChooser;

import jdo.sys.Operator;
import jdo.sys.SystemTool;


import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.ui.TButton;
import com.dongyang.ui.TLabel;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: 物资字典批量导入
 * </p>
 *
 * <p>
 * Description: 物资字典批量导入
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author cehnxi
 * @version 1.0
 */

public class INVBaseBatchControl extends TControl {
	TButton btnChgPrice;
	TLabel  labTip;
	String  type ;

	/**
	 * 初始化
	 */
	public void onInit() { // 初始化程序
		super.onInit();
		btnChgPrice = (TButton) this.getComponent("ACTIVE_Y");
		labTip = (TLabel) this.getComponent("tLabel_0");
		type = "N" ;

	}

	/**
     * 插入Inv_base
     */
	public void onInsertClicked() {
		type ="INSERT" ; 
		JFileChooser fileChooser = new JFileChooser(); 
		int option = fileChooser.showOpenDialog(null);
		TParm newParm = new TParm();
		if (option == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			Workbook wb;
			try {
				wb = Workbook.getWorkbook(file);

				Sheet st = wb.getSheet(0);
				int row = st.getRows();
				int column = st.getColumns();
				String id = "";
				String idName = "" ;

				for (int j = 0; j < column; j++) {

					for (int i = 1; i < row; i++) {

						Cell cell = st.getCell(j, i);
						// ==================  校验数据完成性
						if(!check(cell.getContents().trim(), i,j)){
							return  ;	
						}
									
						idName = st.getCell(j, 0).getContents().trim() ;
							id = cell.getContents().trim();
							newParm.addData(idName, id);
					
					}
					newParm.setCount(row - 1);
				}
			} catch (BiffException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}       

			 System.out.println("-----------excel文件记录-------------"+newParm);
			System.out.println("-----------新增记录总数-------------"
					+ (newParm.getCount()));
			String tmpInsSQL;
           for(int i=0;i<newParm.getCount();i++){
        	// 插入新记录
				tmpInsSQL = "INSERT INTO INV_BASE(INV_CODE,ACTIVE_FLG,INVTYPE_CODE,INVKIND_CODE,INV_CHN_DESC, PY1, PY2,";
				tmpInsSQL += "INV_ENG_DESC,INV_ABS_DESC,DESCRIPTION,MAN_CODE,MAN_NATION,BUYWAY_CODE,";
				tmpInsSQL += "USE_DEADLINE,COST_PRICE,ORDER_CODE,STOCK_UNIT,DISPENSE_UNIT,";
				tmpInsSQL += "HYGIENE_TRADE_CODE,STOPBUY_FLG,NORMALDRUG_FLG,REQUEST_FLG,SEQMAN_FLG,VALIDATE_FLG,";
				tmpInsSQL += "EXPENSIVE_FLG,OPT_USER,OPT_DATE,OPT_TERM,CONSIGN_FLG,CONSIGN_MAN_CODE,";
				tmpInsSQL += "SUP_CODE,  UP_SUP_CODE,  INV_KIND, INV_ACCOUNT ";
				tmpInsSQL += ")";
				tmpInsSQL += " VALUES('" + newParm.getValue("INV_CODE",i)+ "',";
				tmpInsSQL += "'" + newParm.getValue("ACTIVE_FLG",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("INVTYPE_CODE",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("INVKIND_CODE",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("INV_CHN_DESC",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("PY1",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("PY2",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("INV_ENG_DESC",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("INV_ABS_DESC",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("DESCRIPTION",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("MAN_CODE",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("MAN_NATION",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("BUYWAY_CODE",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("USE_DEADLINE",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("COST_PRICE",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("ORDER_CODE",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("STOCK_UNIT",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("DISPENSE_UNIT",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("HYGIENE_TRADE_CODE",i)
						+ "',";
				tmpInsSQL += "'" + newParm.getValue("STOPBUY_FLG",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("NORMALDRUG_FLG",i) + "',";  
				tmpInsSQL += "'" + newParm.getValue("REQUEST_FLG",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("SEQMAN_FLG",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("VALIDATE_FLG",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("EXPENSIVE_FLG",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("OPT_USER",i) + "',";
				tmpInsSQL += "SYSDATE,";
				tmpInsSQL += "'" + newParm.getValue("OPT_TERM",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("CONSIGN_FLG",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("CONSIGN_MAN_CODE",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("SUP_CODE",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("UP_SUP_CODE",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("INV_KIND",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("INV_ACCOUNT",i) + "' ";
				tmpInsSQL += ")";
//				System.out.println("---tmpInsSQL------" + tmpInsSQL);
				TParm insParm = new TParm(this.getDBTool().update(tmpInsSQL));
				if (insParm.getErrCode() < 0) {
					System.out.println("---------插入出错数据inv_CODE:"
							+ newParm.getValue("INV_CODE", i));
					System.out.println("---------SQL:" + tmpInsSQL);
					this.messageBox("执行失败") ;
				}
				// 在事务结束
			}   
           }
			this.messageBox("处理成功！");       
		
		}



	/**
	 * 返回数据库操作工具
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}
/**
 * 校验
 */
    public boolean check(String date,int i,int j){
    	int n = i+1 ;
    	if (StringUtil.isNullString(date) && j==0) {			
			this.messageBox("第"+n+"行第"+1+"列格式有错误") ;
			return false ;
		}
		return true ;
    }
}
