package com.javahis.ui.inv;

import jdo.inv.INVClassifyDetailTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title: 物资分类明细表
 * </p>
 * 
 * <p>
 * Description: 物资分类明细表
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author donglt 2016 03 11
 * @version 1.0
 */


public class INVClassifyDetailControl extends TControl {
		
		TTable table;
		int selectRow = -1;

		/**
		 * 初始化方法
		 */
		public void onInit() {
			
			table = (TTable) this.getComponent("TABLE");// 获取表中的数据
			
		}
		
	
		
		/**
		 * 查询方法
		 */
		public void onQuery() {
			

			TParm parm = new TParm();
			
			// 按物资类别
			if (getValueString("CATEGORY_CODE").length() > 0)
				parm.setData("CATEGORY_CODE", getValueString("CATEGORY_CODE"));
			
			//按供应厂商
			if (getValueString("SUP_CODE").length() > 0)
				parm.setData("SUP_CODE", getValueString("SUP_CODE"));
			System.out.println(""+parm);
  
			TParm result = INVClassifyDetailTool.getInstance().onQuery(parm);
			//查到的数据放到前台
//			this.messageBox("%%%%%5"+result);
			table.setParmValue(result);
			// 数据校验
			if (result.getErrCode() < 0) {
				this.messageBox("查询失败");
				return;  
			}
			if (result.getCount() <= 0) {
				this.messageBox("未查询到数据");
				return;
			}


		}



		/**
		 *清除方法
		 */
		public void onClear() {

			this.clearValue("SUP_CODE;CATEGORY_CODE");

			table.setParmValue(new TParm());
		}
		
		
		/**
		 *导出方法
		 */
		public void onExport() {
			if (table.getRowCount() <= 0) {
				messageBox("无导出资料");
				return;
			}
			ExportExcelUtil.getInstance().exportExcel(table, "物资分类明细表");
		}

		/**
		 * 打印方法
		 */
		public void onPrint() {
			TParm parm = new TParm();
			parm.setData("TITLE", "TEXT", "天津津南红磡领世郡医院物资分类明细");
			parm.setData("WZLB","TEXT", this.getText("CATEGORY_CODE"));
			parm.setData("GYCJ", "TEXT", this.getText("SUP_CODE"));

			parm.setData("NAME", "TEXT", "制表人: " +Operator.getName());

			TParm resultParm = table.getParmValue();
//			this.messageBox(""+resultParm);
//			
			resultParm.addData("SYSTEM", "COLUMNS", "INV_CODE");// 1
			resultParm.addData("SYSTEM", "COLUMNS", "INV_CHN_DESC");// 2
			resultParm.addData("SYSTEM", "COLUMNS", "DESCRIPTION");// 3
//			resultParm.addData("SYSTEM", "COLUMNS", "SUP_CHN_DESC");// 4
			resultParm.addData("SYSTEM", "COLUMNS", "MAN_CHN_DESC");// 5
			resultParm.addData("SYSTEM", "COLUMNS", "CONTRACT_PRICE");// 6
			resultParm.addData("SYSTEM", "COLUMNS", "COST_PRICE");// 7
			
//			parm.setData("TABLE", table.getParmValue().getData());
			
			parm.setData("TABLE", resultParm.getData());
		
			this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVClassifyDetail.jhw",parm);
		}

		public void onDeptSelect() {
			this.clearValue("DEPT_CODE");

		}
	   
		

	}

	


