package com.javahis.ui.dev;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;

import jdo.sys.SystemTool;

import java.awt.Color;

import jdo.sys.Operator;

import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.TTextField;
import com.dongyang.util.StringTool;

import com.dongyang.jdo.TJDODBTool;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: 设备维护记录
 * </p>
 * 
 * <p>
 * Description: 设备维护记录
 * </p>
 * <p>
 * Copyright: ProperSoft 2015
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author wangjc
 * @version 1.0
 */
public class DEVMaintenanceLogControl extends TControl {
	
	/**
	 * 初始化
	 */
	public void onInit(){
		TParm parm = new TParm();
		//sysID.equals("MRO")
//		((TMenuItem) getComponent("save")).setEnabled(false);
		getTextField("DEV_CODE").setPopupMenuParameter("",
	            getConfigParm().newConfig("%ROOT%\\config\\dev\\DEVBasePopup.x"),
	            parm);
	    // 定义接受返回值方法
	    getTextField("DEV_CODE").addEventListener(
	            TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
	    
	    //初始化查询区间
	    this.setValue("START_DATE", StringTool.rollDate(SystemTool
				.getInstance().getDate(), -7));
		this.setValue("END_DATE", SystemTool.getInstance().getDate());
//		this.setValue("NEXT_MTN_DATE", SystemTool.getInstance().getDate());
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
		TTable tableM = this.getTable("TABLEM");
//		System.out.println("SelectedRow:"+tableM.getSelectedRow());
		String nextMtnDate = this.getValueString("NEXT_MTN_DATE");
		if(nextMtnDate.equals("")){
			this.messageBox("下次保养日期不能为空!");
			return;
		}
//		System.out.println("nextMtnDate:"+nextMtnDate);
		TParm mParm = tableM.getParmValue();
//		System.out.println("mParm:"+mParm);
		TParm parm = new TParm();
		int n=1;
		for(int i=0;i<mParm.getCount();i++){
			if(mParm.getValue("SELECT_FLG", i).equals("Y")){
				parm.addData("DEV_CODE", mParm.getValue("DEV_CODE", i));
				parm.addData("MTN_KIND", mParm.getValue("MTN_KIND", i));
				parm.addData("MTN_TYPE_CODE", mParm.getValue("MTN_TYPE_CODE", i));
				parm.addData("DEVSEQ_NO", mParm.getValue("DEVSEQ_NO", i));
				parm.addData("NEXT_MTN_DATE", nextMtnDate.substring(0, 19).replace("-", "/"));
				parm.addData("OPT_USER", Operator.getID());
				parm.addData("OPT_TERM", Operator.getIP());
				parm.setCount(n);
				n++;
			}
		}
//		System.out.println("parm=="+parm);
		if(parm.getCount() <= 0){
			this.messageBox("没有要保存的数据！");
			return;
		}
		TParm result = TIOM_AppServer.executeAction("action.dev.DevAction",
				"onUpdateMaintenance", parm);
		if (result.getErrCode() < 0) {
			this.messageBox("保存失败");
			err(result.getErrText());
			return;
		}
		this.messageBox("保存成功");
		this.onClear();
	}
	
	/**
	 * 查询
	 */
    public void onQuery(){
    	String sql = "  SELECT A.MTN_NO,A.SEQ,A.DEPT_CODE,A.DEV_CODE,A.MTN_KIND,A.MTN_TYPE_CODE,A.MTN_DATE, "
    			+ " A.MTN_HOUR,A.MTN_ENGINEER,A.MTN_RESULT,A.MTN_EVALUATION,A.FILE_PATH,A.FILE_NAME, "
    			+ " A.OPT_USER,A.OPT_DATE,A.OPT_TERM,A.DEV_CODE_DETAIL,B.DEPT_CODE,C.DEV_CHN_DESC "
    			+ " FROM DEV_MAINTENANCE_RECORD A, "
    			+ " DEV_STOCKDD B, "
    			+ " DEV_BASE C "
    			+ " WHERE     A.DEV_CODE = B.DEV_CODE "
    			+ " AND A.DEV_CODE_DETAIL=B.DEV_CODE_DETAIL "
    			+ " AND A.DEV_CODE = C.DEV_CODE ";
    	String startDate = this.getValueString("START_DATE");//开始时间
		String endDate = this.getValueString("END_DATE");//结束时间
		String deptCode = this.getValueString("DEPT_CODE");
		String devseqNo = this.getValueString("DEVSEQ_NO");//资产编号
		String dev_code = this.getValueString("DEV_CODE");//设备代码
		String mtnKind = this.getValueString("MTN_KIND");
		if(!dev_code.equals("")){
			sql += " AND A.DEV_CODE='"+dev_code+"' ";
		}
		if(!deptCode.equals("")){
			sql += " AND B.DEPT_CODE='"+deptCode+"' ";
		}
		if(!startDate.equals("") && !endDate.equals("")){
			sql += " AND A.MTN_DATE BETWEEN TO_DATE('"
						+startDate.substring(0, 10)
						+"','YYYY/MM/DD') AND TO_DATE('"
						+ endDate.substring(0, 10)
						+ "','YYYY/MM/DD') ";
		}
		if(!devseqNo.equals("")){
			sql += " AND A.DEV_CODE_DETAIL='"+devseqNo+"' ";
		}
		if(!mtnKind.equals("")){
			sql += " AND A.MTN_KIND='"+mtnKind+"' ";
		}
		sql += " ORDER BY A.DEV_CODE ";
//    	System.out.println("sql>>>>>>>>"+sql);
    	TTable tableM = this.getTable("TABLEM");
    	TParm mParm = new TParm(TJDODBTool.getInstance().select(sql));
    	if(mParm.getCount() <= 0){
    		this.messageBox("未查询到数据");
    		tableM.removeRowAll();
    		return;
    	}
    	for(int i=0;i<mParm.getCount("DEV_CODE");i++){
    		mParm.setData("OPT_DATE", i, mParm.getValue("OPT_DATE", i).substring(0, 19));
    		if(mParm.getValue("MTN_RESULT", i).equals("0")){
    			mParm.setData("MTN_RESULT", i, "正常");
    		}else if(mParm.getValue("MTN_RESULT", i).equals("1")){
    			mParm.setData("MTN_RESULT", i, "异常");
    			tableM.setRowColor(i, Color.RED);
    		}
    		String mtnDescSql = "";
    		if(mParm.getValue("MTN_KIND", i).equals("2")){
    			mtnDescSql = "SELECT MEASUREM_DESC AS MTN_TYPE_DESC FROM DEV_MEASURE WHERE "
    					+ " DEV_CODE = '"
    					+mParm.getValue("DEV_CODE", i)
    					+ "' AND MEASUREM_CODE = '"
    					+mParm.getValue("MTN_TYPE_CODE", i)+"' ";
    		}else{
    			mtnDescSql = "SELECT MTN_TYPE_DESC FROM DEV_MAINTENANCEM WHERE "
    					+ " DEV_CODE = '"
    					+mParm.getValue("DEV_CODE", i)
    					+ "' AND MTN_KIND = '"
    					+mParm.getValue("MTN_KIND", i)
    					+ "' AND MTN_TYPE_CODE = '"
    					+mParm.getValue("MTN_TYPE_CODE", i)+"' ";
    		}
//    		System.out.println("mtnDescSql-------"+mtnDescSql);
    		TParm mtnDescParm = new TParm(TJDODBTool.getInstance().select(mtnDescSql));
    		mParm.addData("MTN_TYPE_DESC",mtnDescParm.getValue("MTN_TYPE_DESC", 0));
    	}
//    	System.out.println("mParm>>>>>>"+mParm);
    	tableM.setParmValue(mParm);
    }
    
    /**
     * 清空
     */
    public void onClear(){
    	this.clearValue("DEPT_CODE;DEV_CODE;DEV_DESC;DEVSEQ_NO;MTN_KIND");
    	//初始化查询区间
	    this.setValue("START_DATE", StringTool.rollDate(SystemTool
				.getInstance().getDate(), -7));
		this.setValue("END_DATE", SystemTool.getInstance().getDate());
		this.setValue("NEXT_MTN_DATE", SystemTool.getInstance().getDate());
    	TTable tableM = this.getTable("TABLEM");
    	tableM.removeRowAll();
    }
    
    /**
     * 查看保养清单
     */
    public void onAddEmrWrite(){
    	TParm result = new TParm();
    	TTable tableM = this.getTable("TABLEM");
    	if(tableM.getSelectedRow()<0){
    		this.messageBox("请选择一行数据！");
    		return;
    	}
    	result = tableM.getParmValue().getRow(tableM.getSelectedRow());
    	if(result.getValue("MTN_KIND").equals("2")){//保养种类为计量
    		this.messageBox("计量设备无维护清单！");
    		return;
    	}
//		System.out.println("result:"+result);
    	this.openDialog("%ROOT%\\config\\dev\\DEVMtnListView.x",result);

    }
    
    /**
     * M表单击事件，单击查询细表信息
     */
//    public void onTableMClick(){
//    	TTable tableM = this.getTable("TABLEM");
//    	int row = tableM.getSelectedRow();
//    	int col = tableM.getSelectedColumn();
//    	if(row < 0){
//    		return;
//    	}else if(col == 0){
//    		if ("N".equals(tableM.getItemString(row, "SELECT_FLG"))) {
//    			tableM.setItem(row, "SELECT_FLG", "Y");
//			} else if ("Y".equals(tableM.getItemString(row, "SELECT_FLG"))){
//				tableM.setItem(row, "SELECT_FLG", "N");
//			}
//    	}
//    }
	
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
    
    /**
     * 得到ComboBox对象
     * @param tagName
     * @return
     */
    private TComboBox getComboBox(String tagName){
    	return (TComboBox) this.getComponent(tagName);
    }
}
