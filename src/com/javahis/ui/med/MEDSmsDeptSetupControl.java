package com.javahis.ui.med;

import jdo.med.MedSmsDeptSetupTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;


public class MEDSmsDeptSetupControl extends TControl {

	public static String SQL = "";
	/**
	 * TABLE
	 */
	private static String TABLE = "TABLE";
	
	// 记录表的选中行数
    int selectedRowIndex = -1;
    
	TTable table ;
	
	private TParm data;
	
	public void onInit() {
		super.onInit();
		table=(TTable)this.getComponent(TABLE); 
		callFunction("UI|Table|addEventListener",
                "Table->" + TTableEvent.CLICKED, this, "onTableClicked");
		initTable();
	}
	
	public void initTable(){
		callFunction("UI|DELETE|setEnabled", false);
        callFunction("UI|REGION_CODE|setEnabled", true);
        callFunction("UI|DEPT_CODE|setEnabled", true);
        callFunction("UI|COMPETENT_TYPE|setEnabled", true);
		onQuery();
	}
	
	public TTable getTable(String tableName){
		return  (TTable)this.getComponent(tableName);
	}
	
	 
	
	/**
	 * 查询事件 
	 */
	public void onQuery(){
		 TParm selectCondition = getParmForTag("DEPT_CODE;COMPETENT_TYPE", true);
		 data = MedSmsDeptSetupTool.getInstance().onQuery(selectCondition);
         if (data.getErrCode() < 0) {
             messageBox(data.getErrText());
             return;
         }else {
            callFunction("UI|Table|setParmValue", new Object[] {
                         data
            });
            return;
        }
	}
	
	/**
	 * 保存事件
	 */
	public void onSave(){
		// 判断必填字段
        if (this.getValue("REGION_CODE").equals("")
            || this.getValue("DEPT_CODE").equals("")
            || this.getValue("COMPETENT_TYPE").equals("")
            || this.getValue("PERSON_CODE").equals("")) {
            this.messageBox("区域或者科室或者主管分类或者人员必选");
            return;
        }
        // -----------给参数付值-----------------
        TParm parm = new TParm();
        parm.setData("REGION_CODE", this.getValue("REGION_CODE"));
        parm.setData("DEPT_CODE", this.getValue("DEPT_CODE"));
        parm.setData("COMPETENT_TYPE", this.getValue("COMPETENT_TYPE"));
        parm.setData("PERSON_CODE", this.getValue("PERSON_CODE")); // 列表框
        parm.setData("SEQ", this.getValue("SEQ"));
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        
        // -----------给参数付值结束-----------------
        // 判断“REGION_CODE”控件是否是只读属性，如果是则“修改”，否则“新建”
        TParm result = new TParm();
        if ( ( (TTextFormat)this.getComponent("DEPT_CODE")).isEnabled()) {
             
            result = MedSmsDeptSetupTool.getInstance().onInsert(parm);
            this.messageBox("新建成功！");
            onClear();
            initTable();
        }
        else {
            result = MedSmsDeptSetupTool.getInstance().onUpdate(parm);
            this.onClear();
            this.initTable();
            this.messageBox("修改成功！");
            this.getTable("TABLE").setSelectedRow(selectedRowIndex);
        }
        if (result.getErrCode() < 0) {
            this.messageBox("错误！", result.getErrText(), -1);
            return;
        }

	}
	
	
	 public void onTableClicked(int row) {
        callFunction("UI|DELETE|setEnabled", true);
        if (row < 0) {
            return;
        }
        else {
            setValueForParm("REGION_CODE;DEPT_CODE;COMPETENT_TYPE;PERSON_CODE;SEQ",
                            data, row);
            selectedRowIndex  = row;
            callFunction("UI|REGION_CODE|setEnabled", new Object[] {
                         Boolean.valueOf(false)
            });
            callFunction("UI|DEPT_CODE|setEnabled", new Object[] {
                         Boolean.valueOf(false)
            });
            callFunction("UI|COMPETENT_TYPE|setEnabled", new Object[] {
                         Boolean.valueOf(false)
            });
            return;
        }
    }
	
	
	/**
	 * 删除事件
	 */
	public void onDelete(){
		 // 确认删除
        if (this.messageBox("询问", "确定删除?", 0) == 1) {
            return;
        }
        // 检查删除信息不能为空
        if (this.getValue("DEPT_CODE").equals("")
            || this.getValue("DEPT_CODE").equals(null)) {
            this.messageBox("请选择要删除的项！");
            return;
        }
        TParm parm = new TParm();
        parm.setData("DEPT_CODE", this.getValue("DEPT_CODE"));
        parm.setData("COMPETENT_TYPE",this.getValue("COMPETENT_TYPE"));
        
        TParm result = MedSmsDeptSetupTool.getInstance().onDelete(parm);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return;
        }
        this.messageBox("删除成功！");
        this.onClear();
        this.initTable();
	}
	
	/**
	 * 清空事件
	 */
	public void onClear(){
		this
        .clearValue("REGION_CODE;DEPT_CODE;COMPETENT_TYPE;PERSON_CODE;SEQ");
		selectedRowIndex = -1;
		callFunction("UI|DELETE|setEnabled", false);
	    callFunction("UI|REGION_CODE|setEnabled", true);
	    callFunction("UI|DEPT_CODE|setEnabled", true);
	    callFunction("UI|COMPETENT_TYPE|setEnabled", true);
		this.getTable("TABLE").clearSelection(); // 清空TABLE选中状态
		
	}
	
	
	
	/**
	 * getDBTool 数据库工具实例
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}
}
