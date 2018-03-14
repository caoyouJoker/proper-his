package com.javahis.ui.spc;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;

import jdo.spc.SPCCodeMapHisTool;
import jdo.spc.SPCSQL;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title:物联网医院商药品编码比对
 * </p>
 * 
 * <p>
 * Description:物联网医院商药品编码比对
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * 
 * <p>
 * Company: bluecore
 * </p>
 * 
 * @author liuzhen 2013.1.16
 * @version 1.0
 */
public class SPCCodeMapHisControl extends TControl {

	private TTable table; // table
	private TTextField order_code;// 物联网药品代码
	private TTextField order_desc;// 物联网药品名称
	private TTextField order_spe;// 物联网药品规格

	private TComboBox region_code;// 供应商代码
	private TTextField his_order_code;// 供应商药品代码

	/** 构造器 */
	public SPCCodeMapHisControl() {
		super();
	}

	/** 初始化方法 */
	public void onInit() {
		table = (TTable) getComponent("TABLE_M");

		order_code = (TTextField) getComponent("ORDER_CODE");// 物联网药品代码
		order_desc = (TTextField) getComponent("ORDER_DESC");
		;// 物联网药品名称
		order_spe = (TTextField) getComponent("SPECIFICATION");
		;// 物联网药品规格

		region_code = (TComboBox) getComponent("REGION_CODE");
		;// 供应商代码
		his_order_code = (TTextField) getComponent("HIS_ORDER_CODE");
		;// 供应商药品代码
		TParm parm = new TParm();
		parm.setData("CAT1_TYPE", "PHA");
		getTextField("ORDER_CODE")

				.setPopupMenuParameter(
						"UD",
						getConfigParm().newConfig(
								"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// 定义接受返回值方法
		getTextField("ORDER_CODE").addEventListener(
				TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
	}

	/** 查询方法 */
	public void onQuery() {
		TParm parm = new TParm();
		parm.setData("ORDER_CODE", order_code.getValue());

		parm.setData("REGION_CODE", region_code.getValue());
		
		parm.setData("HIS_ORDER_CODE", his_order_code.getValue());

		TParm result = SPCCodeMapHisTool.getInstance().query(parm);

		if (result.getCount() <= 0) {
			messageBox("查询无结果！");
		}
		table.setParmValue(result);
	}

	/**
	 * 保存修改操作
	 */
	public void onSave() {
		//identify by shendr 2013-08-06
		String orderCodeForSync = this.getValueString("ORDER_CODE");
		TParm parm = this
				.getParmForTag("ORDER_CODE;REGION_CODE;HIS_ORDER_CODE;ACTIVE_FLG");

		// 判断是否一个order_code对应多个his_order_code
		TParm backParm = new TParm();
		backParm = SPCCodeMapHisTool.getInstance().queryCount(parm);
		if (backParm.getCount() > 1) {
			messageBox("相同物联网编码不得对应多个医院编码");
			return;
		}
		backParm = SPCCodeMapHisTool.getInstance().queryCounts(backParm);
		if(backParm.getInt("COUNTS") == 1 && "Y".equals(parm.getValue("ACTIVE_FLG"))){
			messageBox("不允许同时启用两笔相同的医院药品编码");
			return;
		}

		if (!checkData(parm))
			return;

		// 检查系统中是否有该药品
		boolean queryFlg = SPCCodeMapHisTool.getInstance().queryBase(parm);
		if (!queryFlg) {
			messageBox("系统中没有该药品！");
			return;
		}

		// 保存之前先查询,数据库中是否有该记录
		int row1 = table.getSelectedRow();
		if (row1 >= 0) {
			parm.setData("ACTIVE_FLG", table.getItemString(table
					.getSelectedRow(), "ACTIVE_FLG"));
		}
		TParm result = SPCCodeMapHisTool.getInstance().updateQuery(parm);
		parm.setData("ACTIVE_FLG", getValueString("ACTIVE_FLG"));
		if (result.getCount("ORDER_CODE") > 0) {
			int row = table.getSelectedRow();
			if (row >= 0) {
				parm.setData("ACTIVE_FLG_UPDATE", table.getItemString(row,
						"ACTIVE_FLG"));
			}
			boolean flg = SPCCodeMapHisTool.getInstance().update(parm);
			if (flg) {
				messageBox("保存成功");
				updatePhaSync(orderCodeForSync);
				onClear();
				onQuery();
				return;
			} else {
				messageBox("保存失败");
				return;
			}
		} else {
			boolean flg = false;
			// 判断这个物联网编码是否有对应的数据
			TParm resultSave = SPCCodeMapHisTool.getInstance().queryLastOrder(parm);
			if(resultSave.getCount()>0){
				// 查询上一条记录是否有库存量
				TParm resultQty = SPCCodeMapHisTool.getInstance().queryQtyByOrderCode(parm);
				if(resultQty.getCount()>0){
					String str = "";
					for (int i = 0; i < resultQty.getCount(); i++) {
						str += ""+resultQty.getData("ORG_CHN_DESC",i) + "此药品库存量为：";
						str += resultQty.getData("STOCK_QTY",i);
						str += "\n";
					}
					messageBox(str+"不能执行换码操作!");
					return;
				}else{
					SPCCodeMapHisTool.getInstance().updateActiveFlg(parm);
					flg = SPCCodeMapHisTool.getInstance().save(parm);
				}
			} else {
				flg = SPCCodeMapHisTool.getInstance().save(parm);
			}
			if (flg) {
				messageBox("保存成功");
				updatePhaSync(orderCodeForSync);
				onClear();
				onQuery();
				return;
			}
			messageBox("保存失败");
		}
	}
	
	/**
     * 同步PHA_BASE和PHA_TRANSUNIT
     * @author shendr 2013.09.02
     */
    public void updatePhaSync(String order_code){
    	TParm tParm = new TParm();
    	tParm.setData("ORDER_CODE", order_code);
    	TParm parm = SPCCodeMapHisTool.getInstance().queryPha(tParm);
    	String flg = "";
    	int row = table.getSelectedRow();
    	if(row <0)
    		flg = "insert";
    	else
    		flg = "update";
    	parm.setData("FLG", flg);
    	TParm hisParm = SPCSQL.getOrderCode(order_code);
    	// 如果没有HIS编码，不做同步
    	if(hisParm.getCount()<=0)
    		return;
    	else{
    		String hisOrderCode =hisParm.getValue("HIS_ORDER_CODE");
        	TParm isExist = SPCSQL.queryOrderCodeIsExistPha(hisOrderCode);
        	// 如果HIS编码在PHA_BASE和PHA_TRANSUNIT中没有，不做同步
        	if(isExist.getCount()<=0)
        		return;
        	else{
        		parm.setData("ORDER_CODE", hisOrderCode);
        		// 调用HIS接口执行同步工作
            	TParm result = TIOM_AppServer.executeAction(
                        "action.spc.SPCPhaBaseSyncExecute", "executeSync", parm);
            	this.messageBox("同步执行完毕!");
            	System.out.println(result.getData("RESULT_MEG"));
        	}
    	}
    }

	/** 删除操作 */
	public void onDelete() {

		table.acceptText();
		int rowno = table.getSelectedRow();

		if (rowno < 0) {
			messageBox("请选择要删除的信息");
			return;
		}

		TParm parm = table.getParmValue().getRow(rowno);

		if (this.messageBox("提示", "是否删除该记录", 2) == 0) {
			boolean flg = SPCCodeMapHisTool.getInstance().delete(parm);

			if (flg) {
				messageBox("删除成功");
				onClear();
				onQuery();
				return;
			}
			messageBox("删除失败");
		}

	}

	/** table 单击事件 */
	public void onTableClick() {

		table.acceptText();
		int rowno = table.getSelectedRow();

		setValueForParm(
				"ORDER_CODE;ORDER_DESC;SPECIFICATION;REGION_CODE;HIS_ORDER_CODE;ACTIVE_FLG",
				table.getParmValue(), rowno);
		order_code.setEnabled(false);

		if (null == region_code.getValue()
				|| "".equals(region_code.getValue().trim())) {
			region_code.setEnabled(true);
		} else {
			region_code.setEnabled(false);
		}

	}

	/** 导入药品对照表 */
	public void onInsertPatByExl() {
		JFileChooser fileChooser = new JFileChooser();
		int option = fileChooser.showOpenDialog(null);

		TParm parm = new TParm();

		if (option == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			try {
				Workbook wb = Workbook.getWorkbook(file);
				Sheet st = wb.getSheet(0);
				int row = st.getRows();
				int column = st.getColumns();
				StringBuffer wrongMsg = new StringBuffer();
				String id = "";
				int count = 0;
				for (int i = 1; i < row; i++) {
					String orderCode = st.getCell(0, i).getContents().trim();
					String regionCode = st.getCell(1, i).getContents().trim();
					String hisOrderCode = st.getCell(2, i).getContents().trim();
					parm.addData("ACTIVE_FLG_UPDATE",
							getValueString("ACTIVE_FLG"));
					int row1 = table.getSelectedRow();
					if (row1 >= 0) {
						parm.addData("ACTIVE_FLG", table.getItemString(row1,
								"ACTIVE_FLG"));
					}
					parm.addData("ORDER_CODE", orderCode);
					parm.addData("REGION_CODE", regionCode);
					parm.addData("HIS_ORDER_CODE", hisOrderCode);

					count++;
				}

				parm.setCount(count);
				if (count < 1) {
					this.messageBox("导入数据失败");
					return;
				}

				TParm result = TIOM_AppServer.executeAction(
						"action.spc.SPCCodeMapHisAction", "importMap", parm);

				String flg = result.getValue("FLG");
				if ("Y".equals(flg)) {
					this.messageBox("更新成功！");
					onQuery();

				} else if ("N".equals(flg)) {
					this.messageBox("更新失败！");
					return;
				} else {
					this.messageBox(flg);
				}

			} catch (BiffException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/** 清空操作 */
	public void onClear() {
		String values = "ORDER_CODE;ORDER_DESC;SPECIFICATION;REGION_CODE;HIS_ORDER_CODE";
		this.clearValue(values);
		((TCheckBox)getComponent("ACTIVE_FLG")).setSelected(true);
		order_code.setEnabled(true);
		region_code.setEnabled(true);
	}

	/** 检查数据培训记录 */
	private boolean checkData(TParm parm) {
		// REGION_CODE;HIS_ORDER_CODE
		if (null == parm.getValue("ORDER_CODE")
				|| "".equals(parm.getValue("ORDER_CODE"))) {
			messageBox("物联网药品编码不能为空！");
			return false;
		}
		if (null == parm.getValue("REGION_CODE")
				|| "".equals(parm.getValue("REGION_CODE").toString())) {
			messageBox("院区不能为空！");
			return false;
		}
		if (null == parm.getValue("HIS_ORDER_CODE")
				|| "".equals(parm.getValue("HIS_ORDER_CODE"))) {
			messageBox("院区药品编码不能为空！");
			return false;
		}
		return true;
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
	 * 接受返回值方法
	 * 
	 * @param tag
	 * @param obj
	 */
	public void popReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		String order_code = parm.getValue("ORDER_CODE");
		if (!StringUtil.isNullString(order_code)) {
			getTextField("ORDER_CODE").setValue(order_code);
		}
		String order_desc = parm.getValue("ORDER_DESC");
		if (!StringUtil.isNullString(order_desc)) {
			getTextField("ORDER_DESC").setValue(order_desc);
		}
		String SPECIFICATION = parm.getValue("SPECIFICATION");
		if (!StringUtil.isNullString(SPECIFICATION)) {
			getTextField("SPECIFICATION").setValue(SPECIFICATION);
		}
	}

}
