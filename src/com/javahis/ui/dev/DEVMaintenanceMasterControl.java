package com.javahis.ui.dev;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTable;

import jdo.sys.SystemTool;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTableNode;

import jdo.sys.Operator;

import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.TTextField;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import com.dongyang.jdo.TJDODBTool;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: 设备维护主档
 * </p>
 * 
 * <p>
 * Description: 设备维护主档
 * </p>
 * <p>
 * Copyright: BlueCore 2015
 * </p>
 * 
 * <p>
 * Company:BlueCore
 * </p>
 * 
 * @author wangjc
 * @version 1.0
 */
public class DEVMaintenanceMasterControl extends TControl {
	
	private ArrayList<Boolean> updated;// 判断是不是修改过
	
	/**
	 * 初始化
	 */
	public void onInit(){
		TParm parm = new TParm();
		((TMenuItem) getComponent("save")).setEnabled(false);
		this.setValue("ACTIVE_FLG", "Y");
		getTextField("DEV_CODE").setPopupMenuParameter("",
	            getConfigParm().newConfig("%ROOT%\\config\\dev\\DEVBasePopup.x"),
	            parm);
	    // 定义接受返回值方法
	    getTextField("DEV_CODE").addEventListener(
	            TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
	    // 添加侦听事件
        addEventListener("TABLED->" + TTableEvent.CHANGE_VALUE,
                         "onTableDChangeValue");
        //table失去焦点时取消编辑状态
        this.getTable("TABLED").getTable().putClientProperty("terminateEditOnFocusLost",
																	Boolean.TRUE);
        this.updated = new ArrayList<Boolean>();
        this.getTable("TABLED").getTable().getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				// TODO Auto-generated method stub
				if (e.getType() == TableModelEvent.UPDATE) {
			        updated.set(e.getLastRow(), true);
				}
			}
		});
	}
    
    /**
     * 接受返回值方法
     *
     * @param tag
     * @param obj
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        String dev_code = parm.getValue("DEV_CODE");
        if (!StringUtil.isNullString(dev_code)){
        	this.setValue("DEV_CODE", dev_code);
        }
        String dev_desc = parm.getValue("DEV_CHN_DESC");
        if (!StringUtil.isNullString(dev_desc)){
        	this.setValue("DEV_DESC", dev_desc);
        }
    }
	
	/**
	 * 保存
	 */
	public void onSave(){
		TTable tableD = this.getTable("TABLED");
		TParm dParm = tableD.getShowParmValue();
		TTable tableM = this.getTable("TABLEM");
//		System.out.println("SelectedRow:"+tableM.getSelectedRow());
		String mtn_kind = this.getValueString("MTN_KIND");//维护种类
		String dev_code = this.getValueString("DEV_CODE");//设备代码
		String mtn_type_desc = this.getValueString("MTN_TYPE_DESC");//维护类别名称
		int mtn_cycle = this.getValueInt("MTN_CYCLE");//周期
		String mtn_unit = this.getValueString("MTN_UNIT");//周期单位
		String mtn_type_code = this.getValueString("MTN_TYPE_CODE");//维护类别代码
		if(mtn_kind.equals("")){
			this.messageBox("维护种类不能为空!");
			return;
		}
		if(dev_code.equals("")){
			this.messageBox("设备代码不能为空!");
			return;
		}
		if(mtn_type_desc.equals("")){
			this.messageBox("维护类别名称不能为空!");
			return;
		}
		if(mtn_cycle == 0){
			this.messageBox("周期不能为0!");
			return;
		}
		if(mtn_unit.equals("")){
			this.messageBox("周期单位不能为空!");
			return;
		}
		TParm parm = new TParm();
		int n=1;
		for(int i=0;i<dParm.getCount();i++){
			if((!dParm.getValue("MTN_DETAIL_DESC", i).equals("") 
							&& updated.get(i)) 
					|| (!dParm.getValue("MTN_DETAIL_DESC", i).equals("") 
							&& tableM.getSelectedRow() >= 0)){
				parm.addData("MTN_DETAIL_DESC", dParm.getValue("MTN_DETAIL_DESC", i));
				parm.addData("MTN_KIND", mtn_kind);
				parm.addData("DEV_CODE", dev_code);
				parm.addData("MTN_TYPE_DESC", mtn_type_desc);
				parm.addData("MTN_CYCLE", mtn_cycle);
				parm.addData("MTN_UNIT", mtn_unit);
				parm.addData("MTN_TYPE_CODE", mtn_type_code);
				if(dParm.getValue("MTN_DETAIL_CODE", i).equals("")){
					String mtn_detail_code = SystemTool.getInstance().getNo("ALL", "DEV",
							"MTN_DETAIL_CODE", "MTN_DETAIL_CODE");
//					System.out.println("MTN_DETAIL_CODE:"+mtn_detail_code);
					parm.addData("MTN_DETAIL_CODE", mtn_detail_code);
					parm.addData("INSERT_FLG", "Y");
				}else{
					parm.addData("MTN_DETAIL_CODE", dParm.getValue("MTN_DETAIL_CODE", i));
					parm.addData("INSERT_FLG", "N");
				}
				parm.addData("OPT_USER", Operator.getID());
				parm.addData("OPT_TERM", Operator.getIP());
				parm.addData("ACTIVE_FLG", this.getValue("ACTIVE_FLG"));
				parm.setCount(n);
				n++;
			}
		}
		
//		System.out.println("dParm=="+parm);
		TParm result = TIOM_AppServer.executeAction("action.dev.DevAction",
				"onInsertOrUpdateMaintenanceMaster", parm);
		if (result.getErrCode() < 0) {
			this.messageBox("保存失败");
			err(result.getErrText());
			return;
		}
		this.messageBox("保存成功");
		this.onClear();
	}
	
	/**
	 * 新增
	 */
	public void onNew(){
		String mtn_kind = this.getValueString("MTN_KIND");
		String dev_code = this.getValueString("DEV_CODE");
		String mtn_type_desc = this.getValueString("MTN_TYPE_DESC");
		int mtn_cycle = this.getValueInt("MTN_CYCLE");
		String mtn_unit = this.getValueString("MTN_UNIT");
		TParm parm = new TParm();
		if(mtn_kind.equals("")){
			this.messageBox("维护种类不能为空!");
			return;
		}else{
			parm.addData("MTN_KIND", mtn_kind);
		}
		if(dev_code.equals("")){
			this.messageBox("设备代码不能为空!");
			return;
		}else{
			parm.addData("DEV_CODE", dev_code);
		}
		if(mtn_type_desc.equals("")){
			this.messageBox("维护类别名称不能为空!");
			return;
		}else{
			parm.addData("MTN_TYPE_DESC", mtn_type_desc);
		}
		if(mtn_cycle == 0){
			this.messageBox("周期不能为0!");
			return;
		}else{
			parm.addData("MTN_CYCLE", mtn_cycle);
		}
		if(mtn_unit.equals("")){
			this.messageBox("周期单位不能为空!");
			return;
		}else{
			parm.addData("MTN_UNIT", mtn_unit);
		}
		String devBaseSql = "SELECT MEASURE_FLG,MAINTENANCE_FLG,QUALITY_CONTROL_FLG "
				+ " FROM DEV_BASE WHERE DEV_CODE='"+dev_code+"' ";
//		System.out.println("devBaseSql:"+devBaseSql+"   "+mtn_kind);
		TParm devBaseParm = new TParm(TJDODBTool.getInstance().select(devBaseSql));
		if(devBaseParm.getValue("MAINTENANCE_FLG", 0).equals("N")
				&&mtn_kind.equals("0")){
			this.messageBox("请先在设备基本档中勾选此设备的保养注记并保存!");
			return;
		}else if(devBaseParm.getValue("QUALITY_CONTROL_FLG", 0).equals("N")
				&&mtn_kind.equals("1")){
			this.messageBox("请先在设备基本档中勾选此设备的质控注记并保存!");
			return;
		}
		Timestamp timestamp = SystemTool.getInstance().getDate();
		String mtn_type_code = SystemTool.getInstance().getNo("ALL", "DEV",
                "MTN_TYPE_CODE", "MTN_TYPE_CODE");
		if(mtn_kind.equals("0")){//保养
			mtn_type_code = "B"+mtn_type_code;
		}else if(mtn_kind.equals("1")){//质控
			mtn_type_code = "Z"+mtn_type_code;
		}else if(mtn_kind.equals("2")){//计量
			mtn_type_code = "J"+mtn_type_code;
		}
		parm.addData("MTN_TYPE_CODE", mtn_type_code);
		parm.addData("OPT_USER", Operator.getID());
		parm.addData("OPT_DATE", timestamp);
		parm.addData("OPT_TERM", Operator.getIP());
		parm.addData("ACTIVE_FLG", this.getValue("ACTIVE_FLG"));
		TParm saveParm = new TParm();
		saveParm.setData("DEV_MAINTENANCEM", parm.getData());
		TParm devMtnDate = this.getDevMtnDateParm(parm);
		if(devMtnDate != null){
//			this.messageBox("库存没有此类设备");
//			return;
			saveParm.setData("DEV_MAINTENANCE_DATE", devMtnDate.getData());
		}else{
			saveParm.setData("DEV_MAINTENANCE_DATE", new TParm().getData());
		}
//		System.out.println("parm------------"+saveParm);
		TParm result = TIOM_AppServer.executeAction("action.dev.DevAction",
				"onInsertMaintenanceMaster", saveParm);
		if (result.getErrCode() < 0) {
			this.messageBox("保存失败");
			err(result.getErrText());
			return;
		}
		this.messageBox("保存成功");
		this.onQuery();
	}
	
	/**
	 * 获取DEV_MTN_DATE数据
	 * @param parm
	 * @return
	 */
	public TParm getDevMtnDateParm(TParm parm){
		TParm result = new TParm();
		String detailSql = "SELECT DEV_CODE_DETAIL FROM DEV_STOCKDD WHERE DEV_CODE='"+parm.getValue("DEV_CODE", 0)+"' ";
		TParm detailParm = new TParm(TJDODBTool.getInstance().select(detailSql));
		if(detailParm.getCount()<=0){
			return null;
		}
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd");
		int n = 1;
		for(int i=0;i<detailParm.getCount("DEV_CODE_DETAIL");i++){
			if(parm.getValue("MTN_KIND", 0).equals("0")){//保养
				result.addData("DEV_CODE", parm.getValue("DEV_CODE", 0));
				result.addData("MTN_KIND", parm.getValue("MTN_KIND", 0));
				result.addData("MTN_TYPE_CODE", parm.getValue("MTN_TYPE_CODE", 0));
				result.addData("DEVSEQ_NO", detailParm.getValue("DEV_CODE_DETAIL", i));
				result.addData("NEXT_MTN_DATE", sdf.format(new Date()));
				result.addData("OPT_USER", Operator.getID());
//						result.addData("OPT_DATE", "");
				result.addData("OPT_TERM", Operator.getIP());
				result.setCount(n);
				n++;
			}
			if(parm.getValue("MTN_KIND", 0).equals("1")){//质控
				result.addData("DEV_CODE", parm.getValue("DEV_CODE", 0));
				result.addData("MTN_KIND", parm.getValue("MTN_KIND", 0));
				result.addData("MTN_TYPE_CODE", parm.getValue("MTN_TYPE_CODE", 0));
				result.addData("DEVSEQ_NO", detailParm.getValue("DEV_CODE_DETAIL", i));
				result.addData("NEXT_MTN_DATE", sdf.format(new Date()));
				result.addData("OPT_USER", Operator.getID());
//						result.addData("OPT_DATE", "");
				result.addData("OPT_TERM", Operator.getIP());
				result.setCount(n);
				n++;
			}
		}
		return result;
	}
	
	/**
	 * 查询
	 */
    public void onQuery(){
    	String sql = "SELECT 'N' AS DELETE_FLG, A.ACTIVE_FLG,A.DEV_CODE,A.MTN_KIND,A.MTN_TYPE_CODE,A.MTN_TYPE_DESC,"
    			+ " A.MTN_CYCLE,A.MTN_UNIT,B.DEV_CHN_DESC,A.OPT_USER,A.OPT_DATE,A.OPT_TERM "
    			+ " FROM DEV_MAINTENANCEM A,DEV_BASE B WHERE A.DEV_CODE=B.DEV_CODE ";
    	
    	String mtn_kind = this.getValueString("MTN_KIND");
		String dev_code = this.getValueString("DEV_CODE");
		String mtn_type_desc = this.getValueString("MTN_TYPE_DESC");
		int mtn_cycle = this.getValueInt("MTN_CYCLE");
		String mtn_unit = this.getValueString("MTN_UNIT");
		if(!mtn_kind.equals("")){
			sql += " AND A.MTN_KIND='"+mtn_kind+"' ";
		}
		if(!dev_code.equals("")){
			sql += " AND A.DEV_CODE='"+dev_code+"' ";
		}
		if(!mtn_type_desc.equals("")){
			sql += " AND A.MTN_TYPE_DESC LIKE '%"+mtn_type_desc+"%' ";
		}
		if(mtn_cycle != 0 && !mtn_unit.equals("")){
			sql += " AND A.MTN_CYCLE = '"+mtn_cycle
					+"' AND A.MTN_UNIT='"+mtn_unit+"' ";
		}else if(mtn_cycle == 0 && !mtn_unit.equals("")){
			this.messageBox("周期与周期单位必须同时填写！");
			return;
		}else if(mtn_cycle != 0 && mtn_unit.equals("")){
			this.messageBox("周期与周期单位必须同时填写！");
			return;
		}
		sql += " ORDER BY A.DEV_CODE";
    	
//    	System.out.println("sql>>>>>>>>"+sql);
    	TTable tableM = this.getTable("TABLEM");
    	TParm mParm = new TParm(TJDODBTool.getInstance().select(sql));
    	if(mParm.getCount() <= 0){
    		this.messageBox("未查询到数据");
    		return;
    	}
    	for(int i=0;i<mParm.getCount("DEV_CODE");i++){
    		mParm.setData("OPT_DATE", i, mParm.getValue("OPT_DATE", i).substring(0, 19));
    	}
//    	System.out.println("mParm>>>>>>"+mParm);
    	tableM.setParmValue(mParm);
    }
    
    /**
     * 清空
     */
    public void onClear(){
    	this.setValue("ACTIVE_FLG", "Y");
    	this.clearValue("MTN_KIND;DEV_CODE;DEV_DESC;MTN_TYPE_CODE;MTN_TYPE_DESC;"
    			+ "MTN_CYCLE;MTN_UNIT");
    	((TMenuItem) getComponent("save")).setEnabled(false);
    	((TMenuItem) getComponent("new")).setEnabled(true);
    	TTable tableM = this.getTable("TABLEM");
    	TTable tableD = this.getTable("TABLED");
    	tableM.removeRowAll();
    	tableD.removeRowAll();
    }
    
    public void onDelete(String tableTag,int row){
    	TTable table = this.getTable(tableTag);
    	TParm parm = table.getParmValue().getRow(row);
//    	System.out.println("parm:"+parm);
    	TParm result = TIOM_AppServer.executeAction("action.dev.DevAction",
				"onDeleteMaintenanceMaster", parm);
		if (result.getErrCode() < 0) {
			this.messageBox("删除失败");
			err(result.getErrText());
			return;
		}
		this.messageBox("删除成功");
		if(parm.getValue("MTN_DETAIL_CODE").equals("") 
        		|| parm.getValue("MTN_DETAIL_CODE") == null){
			this.onClear();
		}else{
			onTableMClick();
		}
    }
    
    /**
     * M表单击事件，单击查询细表信息
     */
    public void onTableMClick(){
    	TTable tableM = this.getTable("TABLEM");
    	TTable tableD = this.getTable("TABLED");
    	int row = tableM.getSelectedRow();
    	int column = tableM.getSelectedColumn();
    	if(row < 0){
    		return;
    	}
    	TParm mParm = tableM.getParmValue().getRow(row);
    	if(column == 0){
    		if (this.messageBox("提示信息 Tips",
					"删除设备维护主档，是否继续?",
					this.YES_NO_OPTION) != 0){
	        	return;
	        }else{
	        	String sql = "SELECT * FROM DEV_MAINTENANCE_DATE "
	        			+ " WHERE MTN_TYPE_CODE='"+mParm.getValue("MTN_TYPE_CODE")+"' ";
	        	TParm mtnDateParm = new TParm(TJDODBTool.getInstance().select(sql));
	        	if(mtnDateParm.getCount()>0){
	        		this.messageBox("此数据已被使用，禁止删除，可选择停用！");
	        		return;
	        	}
	        	this.onDelete("TABLEM",row);
	        }
    	}
    	((TMenuItem) getComponent("save")).setEnabled(true);
    	((TMenuItem) getComponent("new")).setEnabled(false);
    	
    	this.setValue("MTN_KIND", mParm.getValue("MTN_KIND"));
    	this.setValue("DEV_CODE", mParm.getValue("DEV_CODE"));
    	this.setValue("DEV_DESC", mParm.getValue("DEV_CHN_DESC"));
    	this.setValue("MTN_TYPE_CODE", mParm.getValue("MTN_TYPE_CODE"));
    	this.setValue("MTN_TYPE_DESC", mParm.getValue("MTN_TYPE_DESC"));
    	this.setValue("MTN_CYCLE", mParm.getValue("MTN_CYCLE"));
    	this.setValue("MTN_UNIT", mParm.getValue("MTN_UNIT"));
    	this.setValue("ACTIVE_FLG", mParm.getValue("ACTIVE_FLG"));
    	String sql = "SELECT 'N' AS DELETE_FLG,DEV_CODE,MTN_KIND,MTN_TYPE_CODE,MTN_DETAIL_CODE,MTN_DETAIL_DESC,"
    			+ " ACTIVE_FLG,OPT_USER,OPT_DATE,OPT_TERM FROM DEV_MAINTENANCED "
    			+ " WHERE DEV_CODE='"+mParm.getValue("DEV_CODE")
    			+ "' AND MTN_KIND='"+mParm.getValue("MTN_KIND")
    			+ "' AND MTN_TYPE_CODE='"+mParm.getValue("MTN_TYPE_CODE")+"' "
    			+ " ORDER BY MTN_DETAIL_CODE";
//    	System.out.println("sql>>>>>>>>"+sql);
    	TParm dParm = new TParm(TJDODBTool.getInstance().select(sql));
    	for(int i=0;i<dParm.getCount("DEV_CODE");i++){
    		dParm.setData("OPT_DATE", i, dParm.getValue("OPT_DATE", i).substring(0, 19));
    		updated.add(false);// 没有修改过
    	}
//    	System.out.println("mParm>>>>>>"+mParm);
    	tableD.setParmValue(dParm);
    	tableD.addRow();
    	updated.add(false);// 没有修改过
    	tableD.acceptText();
    }
    
    /**
     * D表单击事件
     */
    public void onTableDClick(){
//    	TTable tableM = this.getTable("TABLEM");
    	TTable tableD = this.getTable("TABLED");
    	int row = tableD.getSelectedRow();
    	int column = tableD.getSelectedColumn();
    	if(row < 0){
    		return;
    	}
    	if(column == 0){
    		if (this.messageBox("提示信息 Tips",
					"删除设备维护主档，是否继续?",
					this.YES_NO_OPTION) != 0){
	        	return;
	        }else{
	        	this.onDelete("TABLED",row);
	        }
    	}
    }
    
    /**
     * 表格值改变事件
     *
     * @param obj
     *            Object
     */
    public boolean onTableDChangeValue(Object obj) {
        // 值改变的单元格
        TTableNode node = (TTableNode) obj;
        if (node == null)
            return false;
        // 判断数据改变
        if (node.getValue() != null && node.getOldValue() != null &&
            node.getValue().equals(node.getOldValue()))
            return true;
        // Table的列名
        TTable table = node.getTable();
        String columnName = table.getDataStoreColumnName(node.getColumn());
        int row = node.getRow();
//        System.out.println(row+"-------"+table.getRowCount());
        if ("MTN_DETAIL_DESC".equals(columnName)) {
//            double qty = TypeTool.getDouble(node.getValue());
            if (node.getValue().equals("")) {
                this.messageBox("细项名称不能为空!");
                return true;
            }
            if(row==table.getRowCount()-1){
            	table.addRow();
            	updated.add(false);
            }
            return false;
        }
        return false;
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
     * @param tagName
     * @return
     */
    private TTable getTable(String tagName){
    	return (TTable) this.getComponent(tagName);
    }
}
