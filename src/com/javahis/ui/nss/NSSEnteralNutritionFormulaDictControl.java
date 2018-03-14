package com.javahis.ui.nss;

import java.awt.Component;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import jdo.nss.NSSENFormulaDataStore;
import jdo.nss.NSSEnteralNutritionTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import org.apache.commons.lang.StringUtils;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.TMessage;
import com.javahis.util.StringUtil;

/**
 * <p>Title: 肠内营养配方字典</p>
 *
 * <p>Description: 肠内营养配方字典</p>
 *
 * <p>Copyright: Copyright (c) 2015</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author wangb 2015.3.13
 * @version 1.0
 */
public class NSSEnteralNutritionFormulaDictControl extends TControl {
    public NSSEnteralNutritionFormulaDictControl() {
        super();
    }

    private TTable tableM;
    private TTable tableD;
    private TTextField texFieldOrderCode;
    private NSSENFormulaDataStore dataStore;

    /**
     * 初始化方法
     */
    public void onInit() {
        tableM = getTable("TABLEM");
        tableD = getTable("TABLED");
        
		TParm parm = new TParm(); 
		texFieldOrderCode = (TTextField) this.getComponent("ORDER_CODE");
		texFieldOrderCode.setPopupMenuParameter("UD", getConfigParm()
				.newConfig("%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
    	// 定义接受返回值方法
		texFieldOrderCode.addEventListener(TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
		// 注册激发弹出的事件
        getTable("TABLED").addEventListener(TTableEvent.CREATE_EDIT_COMPONENT,
                                             this, "onCreateEditComoponent");
        onQuery();
    }
    
    /**
	 * 接受返回值方法
	 * @param tag
	 * @param obj
	 * add by lich
	 */
	public void popReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		
		String order_code = parm.getValue("ORDER_CODE");
		if (!StringUtil.isNullString(order_code))
			texFieldOrderCode.setValue("");
			this.setValue("ORDER_CODE",order_code);
		String order_desc = parm.getValue("ORDER_DESC");
		if (!StringUtil.isNullString(order_desc))
			this.setValue("ORDER_DESC",order_desc);
		
	}
    
    /**
     * 查询方法
     */
	public void onQuery() {
		TParm parm = new TParm();
		parm.setData("FORMULA_CODE", this.getValueString("FORMULA_CODE"));
		parm.setData("FORMULA_CHN_DESC", this
				.getValueString("FORMULA_CHN_DESC"));

		TParm resultParm = NSSEnteralNutritionTool.getInstance().selectDataPFM(
				parm);
		if (resultParm.getErrCode() < 0) {
			this.messageBox("查询失败！");
			return;
		}
		tableM.setParmValue(resultParm);
	}
    
	/**
	 * 添加对table 的监听事件
	 * 
	 * @param row
	 */
	public void onTableMClicked(int row) {
		if (row < 0) {
			return;
		}
		
		TTable table = getTable("TABLEM");
    	int tableRow = table.getSelectedRow();
		TParm data = tableM.getParmValue().getRow(table.getSelectedRow());
        this.setValue("FORMULA_CODE", table.getItemData(tableRow,"FORMULA_CODE"));
        this.setValue("FORMULA_CHN_DESC",  table.getItemData(tableRow,"FORMULA_CHN_DESC"));
        this.setValue("FORMULA_ENG_DESC", table.getItemData(tableRow,"FORMULA_ENG_DESC"));
        this.setValue("PY1",  table.getItemData(tableRow,"PY1"));
        this.setValue("PY2", table.getItemData(tableRow,"PY2"));
        this.setValue("ACTIVE_FLG", table.getItemData(tableRow,"ACTIVE_FLG"));
        this.setValue("CLINIC_PROJECT_FLG", table.getItemData(tableRow,"CLINIC_PROJECT_FLG"));
        this.setValue("ORDER_CODE", data.getValue("ORDER_CODE"));
        this.setValue("ORDER_DESC", table.getItemData(tableRow,"ORDER_DESC"));
        this.setValue("MEDI_UNIT", table.getItemData(tableRow,"MEDI_UNIT"));
        this.setValue("NUTRITION_CONTENT", table.getItemData(tableRow,"NUTRITION_CONTENT"));
        this.setValue("NUTRITION_UNIT", table.getItemData(tableRow,"NUTRITION_UNIT"));
      
        callFunction("UI|FORMULA_CODE|setEnabled", false);
        callFunction("UI|FORMULA_CHN_DESC|setEnabled", true);
        callFunction("UI|ORDER_CODE|setEnabled", false);
        
        // 明细信息
		this.getTableFormulaInfo(data);
		// 明细表格增加一行
		this.addRow();
	}
	
	/**
	 * 得到营养成分明细（细表内容）
	 * @param parm
	 * @return
	 */
	private String getDetailSQl(TParm parm) {
		String sql = "SELECT FORMULA_CODE,NUTRITION_CODE,NUTRITION_CONTENT,OPT_USER,OPT_DATE,OPT_TERM" +
				" FROM NSS_EN_FORMULAD" +
				" WHERE FORMULA_CODE = '" + parm.getValue("FORMULA_CODE") + "'";
		return sql;
	}
	
	 /**
     * 明细表格(TABLE_D)单击事件
     */
    public void onTableDClicked() {
        TTable table = getTable("TABLED");
       
        int row = table.getSelectedRow();
        if (row != -1) {
            // 主项信息
            TTable table_M = getTable("TABLEM");
            table_M.setSelectionMode(0);
        }
    }
	
	  /**
     * 保存操作
     * add by lich
     */
	public void onSave() {
		int tabelMSelRow = tableM.getSelectedRow();
		boolean updateMFlg = false;
		TParm parm = new TParm();
		TParm result = new TParm();

		if (getTextField("FORMULA_CODE").isEnabled()) {
			messageBox("如新增数据请点新增按钮");
			return;
		}
		
		if (!validateData()) {
			return;
		}

		parm.setData("FORMULA_CODE", this.getValueString("FORMULA_CODE"));
		// 查询当前肠内营养配方代码是该否存在
		result = NSSEnteralNutritionTool.getInstance().isExistPFM(parm);
		
		if (result.getInt("COUNT", 0) == 0) {
			// 新增
			parm = new TParm();
			this.makeTableMData(parm);

			result = NSSEnteralNutritionTool.getInstance().insertDataPFM(parm);
			
			updateMFlg = true;
		} else {
			if (tabelMSelRow >= 0) {
				// 修改
				parm = new TParm();
				this.makeTableMData(parm);

				result = NSSEnteralNutritionTool.getInstance().updateDataPFM(
						parm);
				
				updateMFlg = true;
			} else {
				// 更新细项
				tableD.acceptText();

				result = new TParm();
				if (!dataStore.update()) {
					result.setErr(-1, "更新营养成分失败");
    			}
			}
		}
		
		if (result.getErrCode() < 0) {
			this.messageBox("保存失败");
			err("ERR:" + result.getErrCode() + result.getErrText());
			return;
		} else {
			this.messageBox("保存成功"); 
			if (updateMFlg) {
				this.onClear();
				this.onQuery();
				
				if (tabelMSelRow > -1) {
					this.onTableMClicked(tabelMSelRow);
				}
			}
		}
	}
    
    /**
     * 组装配方字典主表数据
     */
    private void makeTableMData(TParm parm) {
    	//医嘱代码
		parm.setData("FORMULA_CODE", this.getValueString("FORMULA_CODE"));
		//医嘱中文描述
		parm.setData("FORMULA_CHN_DESC",this.getValueString("FORMULA_CHN_DESC"));
		//开立单位		    			
		parm.setData("MEDI_UNIT",this.getValueString("MEDI_UNIT"));    		
		//营养成分含量
		parm.setData("NUTRITION_CONTENT",this.getValueDouble("NUTRITION_CONTENT"));   		
		//单位   					    			
		parm.setData("NUTRITION_UNIT",this.getValueString("NUTRITION_UNIT"));
		parm.setData("FORMULA_ENG_DESC", this.getValueString("FORMULA_ENG_DESC"));
		parm.setData("PY1", this.getValueString("PY1"));
		parm.setData("PY2", this.getValueString("PY2"));
		parm.setData("ACTIVE_FLG", this.getValueBoolean("ACTIVE_FLG"));
		parm.setData("SHOW_NUTRITION_FLG", this.getValueBoolean("SHOW_NUTRITION_FLG"));
		parm.setData("CLINIC_PROJECT_FLG", this.getValueBoolean("CLINIC_PROJECT_FLG"));
		parm.setData("ORDER_CODE", this.getValueString("ORDER_CODE"));
		parm.setData("ORDER_DESC", this.getValueString("ORDER_DESC"));
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		String date = SystemTool.getInstance().getDate().toString();
		parm.setData("OPT_DATE", date.substring(0, date.length()-2));
    }
    
    /**
     * 检查主表数据
     * @return
     */
    private boolean validateData() {
        if ("".equals(this.getValueString("FORMULA_CHN_DESC"))) {
        	this.messageBox("请填写配方中文名称");
            return false;
        }
        if ("".equals(getValueString("MEDI_UNIT"))) {
            this.messageBox("请填写开立单位");
            return false;
        }
        if ("".equals(getValueString("NUTRITION_CONTENT"))) {    
            this.messageBox("请填写营养成分含量");
            return false;     
        }
        if ("".equals(getValueString("NUTRITION_UNIT"))) {    
            this.messageBox("请填写单位");
            return false;     
        }
        
        return true;
    }
   
    /**
     * 是否是诊疗项目  注记
     */
	public void setEnAble(){
		TCheckBox ClinicProjectFlg = (TCheckBox) getComponent("CLINIC_PROJECT_FLG");
		if(ClinicProjectFlg.isSelected()){
			callFunction("UI|ORDER_CODE|setEnabled", true);
			callFunction("UI|ORDER_DESC|setEnabled", false);
		}else{
			this.clearValue("ORDER_CODE;ORDER_DESC");
			callFunction("UI|ORDER_CODE|setEnabled", false);
			callFunction("UI|ORDER_DESC|setEnabled", false);
		}
	}
	
	 /**
     * 删除操作
     */
    public void onDelete() {
    	int tableMSelRow = tableM.getSelectedRow();
    	int tableDSelRow = tableD.getSelectedRow();
    	boolean checkedFlg = false;
    	TParm parm = new TParm();
    	TParm result = new TParm();
    	
    	if (tableMSelRow > -1) {
    		// 删除主项
    		if (this.messageBox("删除", "是否删除该主项及其包含的细项数据", 2) == 0) {
    			// 先删除主项下的所有细项
    			tableD.getDataStore().deleteRowAll();
    			
    			if (!tableD.getDataStore().update()) {
    				this.messageBox("E0001");
    				return;
    			}
    			
    			// 删除主项
				parm.setData("FORMULA_CODE", tableM.getParmValue().getRow(
						tableMSelRow).getValue("FORMULA_CODE"));
    			result = NSSEnteralNutritionTool.getInstance().deleteDataPFM(parm);
    			
    			if (result.getErrCode() < 0) {
    				this.messageBox("删除主项数据错误");
					err("ERR:" + result.getErrCode() + result.getErrText());
					return;
    			}
    			
    			this.messageBox("P0003");
    			this.onClear();
    			this.onQuery();
    		}
    	} else if (tableDSelRow > -1) {
    		// 强制失去编辑焦点
    		if (tableD.getTable().isEditing()) {
    			tableD.getTable().getCellEditor().stopCellEditing();
    		}
    		
    		parm = tableD.getShowParmValue();
    		int count = parm.getCount("NUTRITION_CHN_DESC");
    		List<Integer> deleteList = new ArrayList<Integer>();
    		
    		// 如果没有选中主项数据，则查看是否选中了细项
    		for (int k = 0; k < count; k++) {
				if (parm.getBoolean("FLG" ,k)) {
					deleteList.add(k);
					checkedFlg = true;
				}
    		}
    		
    		if (checkedFlg) {
    			if (this.messageBox("删除", "是否删除", 2) == 0) {
    				for (int j = deleteList.size() - 1; j > -1; j--) {
						if (StringUtils.isNotEmpty(tableD.getDataStore()
								.getItemString(deleteList.get(j),
										"NUTRITION_CODE"))) {
							tableD.getDataStore().deleteRow(deleteList.get(j));
							tableD.setDSValue();
						}
    				}
    				
        			if (!tableD.getDataStore().update()) {
        				this.messageBox("E0001");
        				return;
        			}
        			
        			this.messageBox("P0003");
        		}
    		}
    	}
    	
    	if (tableMSelRow < 0 && tableDSelRow < 0) {
    		this.messageBox("请选择要删除的数据");
    		return;
    	} else if (tableDSelRow > -1 && !checkedFlg) {
    		this.messageBox("请勾选要删除的细项");
    		return;
    	}
    }
	
	 /**
     * 清空操作
     */
    public void onClear(){
    	this.clearValue("FORMULA_CODE;FORMULA_CHN_DESC;FORMULA_ENG_DESC;PY1;PY2;" +
    			"ACTIVE_FLG;SHOW_NUTRITION_FLG;CLINIC_PROJECT_FLG;ORDER_CODE;" +
    			"ORDER_DESC;MEDI_UNIT;NUTRITION_CONTENT;NUTRITION_UNIT");
    	callFunction("UI|ORDER_CODE|setEnabled", false);
    	callFunction("UI|FORMULA_CODE|setEnabled", true);
    	tableM.setParmValue(new TParm());
    	tableD.setParmValue(new TParm());
    	this.onQuery();
    }
	
    /**
     * 生成新的成分代码主键
     */
    public void onNewFormulaCode(){
    	TParm maxCode = NSSEnteralNutritionTool.getInstance().getMaxFormulaCode();
    	String newCode = getNewCode(maxCode.getValue("MAX", 0));
    	this.setValue("FORMULA_CODE", newCode);
    	callFunction("UI|FORMULA_CODE|setEnabled", false);
    	tableD.removeRowAll();
    }
    
    /**
     * 获取新增最大主键号
     * @param NutritionCode
     * @return
     * add by lich
     */
    private String getNewCode(String Code){
    	DecimalFormat df = new DecimalFormat("0000");
    	double code = 1;
    	try {
    		code = Double.parseDouble(Code);
    		code += 1;		
    		return df.format(code);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return df.format(code);
    }
    
    /**
     * TABOO_CHN_DESC回车事件
     */
    public void onUserNameAction() {
    	String py = TMessage.getPy(this.getValueString("FORMULA_CHN_DESC"));
        setValue("PY1", py);
        ((TTextField) getComponent("PY1")).grabFocus();
    }
    
    /**
     * 当TABLE创建编辑控件时
     *
     * @param com
     * @param row
     * @param column
     */
    public void onCreateEditComoponent(Component com, int row, int column) {
        if (column != 1)
            return;
        if (! (com instanceof TTextField))
            return;
        TParm parm = new TParm();
        parm.setData("NUTRITION_CODE", getValueString("NUTRITION_CODE"));
        TTextField textFilter = (TTextField) com;
        textFilter.onInit();
        // 设置弹出菜单
        textFilter.setPopupMenuParameter("UI", getConfigParm().newConfig(
            "%ROOT%\\config\\nss\\NSSENutrientsDictPop.x"), parm);
        // 定义接受返回值方法
        textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
                                    "popReturnOrder");
    }

    /**
	 * 接受返回值方法细表
	 * @param tag
	 * @param obj
	 * add by lich
	 */
	public void popReturnOrder(String tag, Object obj) {
		TParm parm = (TParm) obj;

	    tableD.acceptText();
	    int selectedRow = tableD.getSelectedRow();
	    String nutritionCode = parm.getValue("NUTRITION_CODE");
	    String nutritionDesc = parm.getValue("NUTRITION_CHN_DESC");
	    
		tableD.getDataStore().setItem(selectedRow, "FORMULA_CODE",
				this.getValueString("FORMULA_CODE"));
		tableD.getDataStore().setItem(selectedRow, "NUTRITION_CODE",
				nutritionCode);
		tableD.setItem(selectedRow, "OPT_USER", Operator.getID());
		tableD.setItem(selectedRow, "OPT_DATE", SystemTool.getInstance()
				.getDate());
		tableD.setItem(selectedRow, "OPT_TERM", Operator.getIP());
	    
	    tableD.getDataStore().setActive(selectedRow, true);
	    
	    int count = tableD.getDataStore().rowCount();
        // 判断是否有重复数据
        for (int i = 0; i < count; i++) {
            if (i == selectedRow) {
                continue;
            }
            if (nutritionCode.equals(tableD.getDataStore().getItemData(i, "NUTRITION_CODE"))) {
                this.messageBox("营养成分:【" + nutritionDesc + "】已存在");
                addRow();
                tableD.removeRow(selectedRow);
                return;
            }
        }
        
		// 增加新行
		this.addRow();
	}
    
	
	/**
     * 根据主项查询配方明细数据
     */
    private void getTableFormulaInfo(TParm parm) {
		// 明细信息
    	tableD.removeRowAll();
    	tableD.setSelectionMode(0);
		String sql = this.getDetailSQl(parm);
		dataStore = new NSSENFormulaDataStore();
		dataStore.setSQL(sql);
		dataStore.retrieve();
		
		tableD.setDataStore(dataStore);
		tableD.setDSValue();
    }
	
    /**
     * 配方明细TABLE加入空行
     */
    private void addRow() {
		// 有未编辑行时返回
		if (!this.isNewRow()) {
			return;
		}
		int row = tableD.addRow();
		dataStore.setActive(row, false);
    }
    
	/**
	 * 是否有未编辑行
	 * 
	 * @return boolean
	 */
	private boolean isNewRow() {
		Boolean flag = false;
		TParm parmBuff = dataStore.getBuffer(dataStore.PRIMARY);
		int lastRow = parmBuff.getCount("#ACTIVE#");
		Object obj = parmBuff.getData("#ACTIVE#", lastRow - 1);
		if (obj != null) {
			flag = (Boolean) parmBuff.getData("#ACTIVE#", lastRow - 1);
		} else {
			flag = true;
		}
		return flag;
	}
	
    /**
     * 得到Table对象
     * @param tagName
     *        元素TAG名称
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable)getComponent(tagName);
    }
    
    /**
     * 得到TTextField对象
     * @param tagName
     *        元素TAG名称
     * @return
     */
    private TTextField getTextField(String tagName) {
        return (TTextField)getComponent(tagName);
    }
}
