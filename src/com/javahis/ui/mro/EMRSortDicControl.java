package com.javahis.ui.mro;

import java.util.regex.Pattern;

import jdo.mro.EMRSortDicTool;



import com.caigen.global.s;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.javahis.ui.sys.SYSOpdComOrderControl;
import com.javahis.util.StringUtil;

public class EMRSortDicControl extends TControl {
	TTable table1; 
	TTable table2;
	/**
     * 初始化
     */ 
    public void onInit() { 
    	
    	table1 = (TTable)this.getComponent("TABLE1");
    	table2 = (TTable)this.getComponent("TABLE2");
    	onQuery();
    }
    /**
     * 查询条件，将条件加入到parm中
     */
    public void onQuery(){
    	
    	String sql = "SELECT EMR_SORTDIC_CODE,EMR_SORTDIC_NAME,PY1,SORT,EMR_SORTDIC_TYPE,REMARK"+
				 " FROM EMR_SORTDIC WHERE '1' = '1'";
    	if(this.getValueString("EMR_SORTDIC_CODE").length()>0){
    		sql +=" AND EMR_SORTDIC_CODE = '"+this.getValueString("EMR_SORTDIC_CODE")+"'";
    	}
    	if(this.getValueString("EMR_SORTDIC_TYPE").length()>0){
    		sql +=" AND EMR_SORTDIC_TYPE = '"+this.getValueString("EMR_SORTDIC_TYPE")+"'";
    	}
    	if(this.getValueString("EMR_SORTDIC_NAME").length()>0){
    		sql +=" AND EMR_SORTDIC_NAME LIKE '%"+this.getValueString("EMR_SORTDIC_NAME")+"%'";
    	}
    	sql += " ORDER BY SORT ASC";
    	
    	
    	TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
    	table1.setParmValue(parm);

    }
    /**
     * 点击table1后
     */
    public void onClickEmrSortDic(){
    	//TParm parm = table1.getParmValue();
    	TParm parm = table1.getParmValue().getRow(table1.getSelectedRow());
    	setValue("EMR_SORTDIC_CODE", parm.getValue("EMR_SORTDIC_CODE"));
    	setValue("EMR_SORTDIC_NAME", parm.getValue("EMR_SORTDIC_NAME"));
    	setValue("PY1", parm.getValue("PY1"));
    	setValue("SORT", parm.getValue("SORT"));
    	setValue("EMR_SORTDIC_TYPE", parm.getValue("EMR_SORTDIC_TYPE"));
    	setValue("REMARK", parm.getValue("REMARK"));
    	
    	String sql = "SELECT ID,EMR_SORTDIC_CODE,SORT,FILENAME_KEYWORD FROM EMR_SORTDIC_DETAIL "+
    				 "WHERE EMR_SORTDIC_CODE=(SELECT EMR_SORTDIC_CODE FROM EMR_SORTDIC "+
    				 "WHERE EMR_SORTDIC_CODE='"+parm.getValue("EMR_SORTDIC_CODE")+"')";
    	System.out.println("machao:"+sql);
    	TParm resultParm = new TParm(TJDODBTool.getInstance().select(sql));
    	System.out.println("machao"+resultParm);
    	//this.messageBox(resultParm+"");
    	//String fileNameKeyWord = resultParm.getValue("FILENAME_KEYWORD",0);
    	//this.messageBox(resultParm.getCount()+"");
    	if(resultParm.getCount()>0){
    		//setValue("EMR_NUM_CODE", resultParm.getValue("EMR_SORTDIC_CODE",0));
    		//setValue("FILENAME_KEYWORD", resultParm.getValue("FILENAME_KEYWORD",0));
    		//setValue("SORTDETAIL_ID", resultParm.getValue("ID",0));
    		table2.setParmValue(resultParm);
    		
    	}else{
    		setValue("EMR_NUM_CODE", "");
    		setValue("FILENAME_KEYWORD", "");
    		setValue("SORTDETAIL_ID", "");
    		table2.setParmValue(new TParm());
    	}
    	callFunction("UI|EMR_SORTDIC_CODE|setEnabled", false);
    	//callFunction("UI|EMR_NUM_CODE|setEnabled", false);
    }
    /**
     * 点击table2后
     */
    public void onClickEmrSortDicDetail(){
    	TParm parm = table2.getParmValue().getRow(table2.getSelectedRow());
    	setValue("FILENAME_KEYWORD", parm.getValue("FILENAME_KEYWORD"));
    	setValue("SORTDETAIL_ID", parm.getValue("ID"));
    	setValue("EMR_NUM_CODE", parm.getValue("EMR_SORTDIC_CODE"));
    	//this.messageBox(parm.getValue("ID"));
    }
    /**
     * 新增按钮
     */
    public void onAdd(){
    	setValue("FILENAME_KEYWORD", "");
    	setValue("EMR_NUM_CODE", "");
    	setValue("SORTDETAIL_ID", "");
    }
    /**
     * 清空方法
     */
    public void onClear(){ 
        // 清空画面内容
        String clearString =
        		"EMR_SORTDIC_CODE;EMR_SORTDIC_NAME;PY1;SORT;EMR_SORTDIC_TYPE;REMARK;FILENAME_KEYWORD;EMR_NUM_CODE;SORTDETAIL_ID";
        clearValue(clearString);
        //table2.removeRow(0);
        //设置页面中的控件EMR_SCOPE_CODE为可编辑状态
        callFunction("UI|EMR_SORTDIC_CODE|setEnabled", true);
        callFunction("UI|EMR_NUM_CODE|setEnabled", true);
        table2.setParmValue(new TParm());
    }
    /**
     * 删除方法
     */
    public void onDelete(){

    	if(!StringUtil.isNullString(this.getValueString("SORTDETAIL_ID"))){
    		if (this.messageBox("询问", "是否删除关键字", 2) == 0) {
        		String sql = "DELETE FROM EMR_SORTDIC_DETAIL WHERE ID='"+this.getValueString("SORTDETAIL_ID")+"'";
        		//System.out.println("mac:"+sql);
        		TParm parm = new TParm(TJDODBTool.getInstance().update(sql));
        		if(parm.getErrCode()<0){
        			this.messageBox("删除失败");
        			return;
        		}
        		table2.setParmValue(new TParm());
        		onClear();
        		onQuery();
        		this.messageBox("删除成功");
        	}
    		return;
    	}else{
    		if (this.messageBox("询问", "是否删除主项", 2) == 0) {
        		String sqlDetail = "DELETE FROM EMR_SORTDIC_DETAIL WHERE EMR_SORTDIC_CODE ='"+this.getValueString("EMR_SORTDIC_CODE")+"'";
//        		
//        		TParm parm = new TParm(TJDODBTool.getInstance().update(sqlDetail));
//        		if(parm.getErrCode()<0){
//        			this.messageBox("删除系项失败");
//        			return;
//        		}
        		String sqlSortDic = "DELETE FROM EMR_SORTDIC WHERE EMR_SORTDIC_CODE ='"+this.getValueString("EMR_SORTDIC_CODE")+"'";
//        		
//        		TParm parm1 = new TParm(TJDODBTool.getInstance().update(sqlSortDic));
//        		if(parm1.getErrCode()<0){
//        			this.messageBox("删除主项失败");
//        			return;
//        		}
    			TParm parm = new TParm();
    			parm.setData("sqlDetail",sqlDetail);
    			parm.setData("sqlSortDic",sqlSortDic);
    			
    			TParm result = TIOM_AppServer.executeAction(
                        "action.emr.EMRSortDicAction",
                        "onDelete", parm);
    			
    			if(result.getErrCode()<0){
    				this.messageBox("删除失败");
        			return;
    			}
        		table2.setParmValue(new TParm());
        		onClear();
        		onQuery();
        		this.messageBox("删除成功");
        	}
    		return;
    	}

    }
    /**
     * 保存方法
     */
    public void onSave(){
    	boolean flag = onFilter();
		if(!flag){
			return;
		}
    	TTextField tt = (TTextField)getComponent("EMR_SORTDIC_CODE");
    	//保存方法主表save
    	if(tt.isEnabled()){
    		String EMR_SORTDIC_CODE = this.getValueString("EMR_SORTDIC_CODE");
    		String EMR_SORTDIC_NAME = this.getValueString("EMR_SORTDIC_NAME");
    		String PY1 = this.getValueString("PY1");
    		String SORT = this.getValueString("SORT");
    		String EMR_SORTDIC_TYPE = this.getValueString("EMR_SORTDIC_TYPE");
    		String REMARK = this.getValueString("REMARK");
    		if(StringUtil.isNullString(PY1)){
    			PY1 = "";
    		}
    		if(StringUtil.isNullString(EMR_SORTDIC_TYPE)){
    			EMR_SORTDIC_TYPE = "";
    		}
    		if(StringUtil.isNullString(REMARK)){
    			REMARK = "";
    		}
    		String sql = "INSERT INTO EMR_SORTDIC " +
    					 "("+"EMR_SORTDIC_CODE"+","+"EMR_SORTDIC_NAME"+","+"PY1"+","+"SORT"+","+"EMR_SORTDIC_TYPE"+","+"REMARK) "+
    					 "VALUES "+
    					 "('"+EMR_SORTDIC_CODE+"','"+EMR_SORTDIC_NAME+"','"+PY1+"','"+SORT+"','"+EMR_SORTDIC_TYPE+"','"+REMARK+"')";
    				
    		//System.out.println("mac:"+sql);	
    		TParm parm = new TParm(TJDODBTool.getInstance().update(sql));
    		if(parm.getErrCode()<0){
    			this.messageBox("保存失败");
    			return;
    		}
    		onClear();
    		onQuery();
    		this.messageBox("保存成功");
    		return;
    	}   	
    	String SORTDETAIL_ID = this.getValueString("SORTDETAIL_ID");
    	boolean flagDeail = onFilterDetail();
    	if(!flagDeail){
			return;
		}
    	if(StringUtil.isNullString(SORTDETAIL_ID)){
    		//新增系表,修改主表
    		String sql = "SELECT MAX(to_number(ID)) AS MAX FROM EMR_SORTDIC_DETAIL";
    		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
    		String maxDetailId = parm.getValue("MAX",0);
    		if(StringUtil.isNullString(maxDetailId)){
    			maxDetailId = "0";
    		}
    		//this.messageBox((Integer.parseInt(maxDetailId)+1)+"");
    		String detailId = (Integer.parseInt(maxDetailId)+1)+"";
    		String EMR_SORTDIC_CODE = this.getValueString("EMR_NUM_CODE");
    		String sort = this.getValueString("SORT");
    		String FILENAME_KEYWORD = this.getValueString("FILENAME_KEYWORD");
    		if(StringUtil.isNullString(sort)){
    			sort = "99";
    		}
    		String intSql = "INSERT INTO EMR_SORTDIC_DETAIL "+
    						"("+"ID"+","+"EMR_SORTDIC_CODE"+","+"SORT"+","+"FILENAME_KEYWORD) "+
    						"VALUES "+
    						"('"+detailId+"','"+EMR_SORTDIC_CODE+"','"+sort+"','"+FILENAME_KEYWORD+"')";
    		//System.out.println("ma:::"+intSql);
    		TParm inserDetailtParm = new TParm(TJDODBTool.getInstance().update(intSql));
    		if(inserDetailtParm.getErrCode()<0){
    			this.messageBox("保存失败");
    			return;
    		}
    		//修改主表
    		
    		String SORTDIC_CODE = this.getValueString("EMR_SORTDIC_CODE");
    		String EMR_SORTDIC_NAME = this.getValueString("EMR_SORTDIC_NAME");
    		String PY1 = this.getValueString("PY1");
    		String SORT = this.getValueString("SORT");
    		String EMR_SORTDIC_TYPE = this.getValueString("EMR_SORTDIC_TYPE");
    		String REMARK = this.getValueString("REMARK");
    		if(StringUtil.isNullString(PY1)){
    			PY1 = "";
    		}
    		if(StringUtil.isNullString(EMR_SORTDIC_TYPE)){
    			EMR_SORTDIC_TYPE = "";
    		}
    		if(StringUtil.isNullString(REMARK)){
    			REMARK = "";
    		}
    		String updateSortsql = "UPDATE EMR_SORTDIC SET "+
    							   "EMR_SORTDIC_NAME= '"+EMR_SORTDIC_NAME+"'"+
    							   ",PY1='"+PY1+"'"+
    							   ",SORT='"+SORT+"'"+
    							   ",EMR_SORTDIC_TYPE='"+EMR_SORTDIC_TYPE+"'"+
    							   ",REMARK='"+REMARK+"'"+
    							   " WHERE EMR_SORTDIC_CODE='"+SORTDIC_CODE+"'";
    		TParm updateSortParm = new TParm(TJDODBTool.getInstance().update(updateSortsql));					   
    		if(updateSortParm.getErrCode()<0){
    			this.messageBox("保存失败");
    			return;
    		}					   
    		this.messageBox("保存成功");
    		onClear();
    		onQuery();
    		return;
    	}else{
    		String SORTDIC_CODE = this.getValueString("EMR_SORTDIC_CODE");
    		String SORT = this.getValueString("SORT");
    		String FILENAME_KEYWORD = this.getValueString("FILENAME_KEYWORD");
    		String detailId = this.getValueString("SORTDETAIL_ID");
    		String updateDetil = "UPDATE EMR_SORTDIC_DETAIL SET "+
    							 "EMR_SORTDIC_CODE='"+SORTDIC_CODE+"'"+
    							 ",SORT='"+SORT+"'"+
    							 ",FILENAME_KEYWORD='"+FILENAME_KEYWORD+"'"+
    							 " WHERE ID='"+detailId+"'";
    		//System.out.println("machao:::"+updateDetil); 
    		TParm updateDetailParm = new TParm(TJDODBTool.getInstance().update(updateDetil));	
    		if(updateDetailParm.getErrCode()<0){
    			this.messageBox("保存失败");
    			return;
    		}		

    		//修改主表

    		String SORTDIC_CODE1 = this.getValueString("EMR_SORTDIC_CODE");
    		String EMR_SORTDIC_NAME = this.getValueString("EMR_SORTDIC_NAME");
    		String PY1 = this.getValueString("PY1");
    		String SORT1 = this.getValueString("SORT");
    		String EMR_SORTDIC_TYPE = this.getValueString("EMR_SORTDIC_TYPE");
    		String REMARK = this.getValueString("REMARK");
    		if(StringUtil.isNullString(PY1)){
    			PY1 = "";
    		}
    		if(StringUtil.isNullString(EMR_SORTDIC_TYPE)){
    			EMR_SORTDIC_TYPE = "";
    		}
    		if(StringUtil.isNullString(REMARK)){
    			REMARK = "";
    		}
    		String updateSortsql = "UPDATE EMR_SORTDIC SET "+
    							   "EMR_SORTDIC_NAME= '"+EMR_SORTDIC_NAME+"'"+
    							   ",PY1='"+PY1+"'"+
    							   ",SORT='"+SORT1+"'"+
    							   ",EMR_SORTDIC_TYPE='"+EMR_SORTDIC_TYPE+"'"+
    							   ",REMARK='"+REMARK+"'"+
    							   " WHERE EMR_SORTDIC_CODE='"+SORTDIC_CODE1+"'";
    		TParm updateSortParm = new TParm(TJDODBTool.getInstance().update(updateSortsql));					   
    		if(updateSortParm.getErrCode()<0){
    			this.messageBox("保存失败");
    			return;
    		}					   
    		this.messageBox("保存成功");
    		onClear();
    		onQuery();
    		return;
    	}

    }
    public void getTable2Data(){
    	String sql = "SELECT EMR_SORTDIC_CODE,SORT,FILENAME_KEYWORD FROM EMR_SORTDIC_DETAIL "+
		 "WHERE EMR_SORTDIC_CODE=(SELECT EMR_SORTDIC_CODE FROM EMR_SORTDIC "+
		 "WHERE EMR_SORTDIC_CODE='"+this.getValue("EMR_SORTDIC_CODE")+"')";
		
		TParm resultParm = new TParm(TJDODBTool.getInstance().select(sql));
		
		//String fileNameKeyWord = resultParm.getValue("FILENAME_KEYWORD",0);
		if(resultParm.getCount()>0){
			setValue("EMR_NUM_CODE", resultParm.getValue("EMR_SORTDIC_CODE",0));
			setValue("FILENAME_KEYWORD", resultParm.getValue("FILENAME_KEYWORD",0));
			table2.setParmValue(resultParm);
		}
		callFunction("UI|EMR_SORTDIC_CODE|setEnabled", false);
		callFunction("UI|EMR_NUM_CODE|setEnabled", false);
    }
    /**
     * 过滤条件主表
     */
    public boolean onFilter(){
    	if(StringUtil.isNullString(this.getValueString("EMR_SORTDIC_CODE"))){
			this.messageBox("请输入编号");
			return false;
		}
		if(StringUtil.isNullString(this.getValueString("EMR_SORTDIC_NAME"))){
			this.messageBox("请输入病历名称");
			return false;
		}
		if(StringUtil.isNullString(this.getValueString("SORT"))){
			this.messageBox("请输入排序号");
			return false;
		}
		if(!isInteger(this.getValueString("SORT"))){
			this.messageBox("排序号请输入整数");
			return false;
		}

		
		return true;
    }
    /**
     * 过滤条件系表
     */
    public boolean onFilterDetail(){
		if(StringUtil.isNullString(this.getValueString("FILENAME_KEYWORD"))){
		this.messageBox("请填写文件关键字");
		return false;
		}
		if(StringUtil.isNullString(this.getValueString("EMR_NUM_CODE"))){
			this.messageBox("请填写文件编码");
			return false;
		}
		return true;
    }
    /**
     * 判断整数
     * @param str
     * @return
     */
    public static boolean isInteger(String str) {    
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");    
        return pattern.matcher(str).matches();    
    }  
    public TParm writeParm(){
    	TParm parm = new TParm();
    	
		TParm emrSortDicParm = new TParm();
		emrSortDicParm.setData("EMR_SORTDIC_CODE",this.getValueString("EMR_SORTDIC_CODE") );
		emrSortDicParm.setData("EMR_SORTDIC_NAME",this.getValueString("EMR_SORTDIC_NAME") );
		emrSortDicParm.setData("PY1",this.getValueString("PY1") );
		emrSortDicParm.setData("SORT",this.getValueString("SORT") );
		emrSortDicParm.setData("EMR_SORTDIC_TYPE",this.getValueString("EMR_SORTDIC_TYPE") );
		emrSortDicParm.setData("REMARK",this.getValueString("REMARK") );
		parm.setData("EMRSORTDIC", emrSortDicParm.getData());
		
		TParm emrSortDicDetailParm = new TParm();
		emrSortDicDetailParm.setData("EMR_SORTDIC_CODE", this.getValueString("EMR_NUM_CODE"));
		emrSortDicDetailParm.setData("SORT", this.getValueString("SORT"));
		emrSortDicDetailParm.setData("FILENAME_KEYWORD", this.getValueString("FILENAME_KEYWORD"));
		parm.setData("EMRSORTDICDETAIL", emrSortDicDetailParm.getData());
		
		return parm;
    }
}
