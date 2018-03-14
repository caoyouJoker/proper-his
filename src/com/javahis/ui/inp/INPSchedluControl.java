package com.javahis.ui.inp;


import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;

/**
 *
 * <p>Title: 会诊排班</p>
 *
 * <p>Description: 会诊诊排班</p>
 *
 * <p>Copyright: Copyright (c) caoyong 2013908</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author caoyong
 * @version 1.0
 */
public class INPSchedluControl extends TControl{
	
      private TTable table ;
    
    /**
     * 初始化
     */
    public void onInit(){
    	//全部查询
    	 callFunction("UI|TABLE|addEventListener","TABLE->"+TTableEvent.CLICKED,this,"onTABLEClicked");//单击事件
    	 //this.addEventListener("TABLE->" + TTableEvent.CHANGE_VALUE,"onROOMTABLEChargeValue");//回车监听
    	 this.setValue("REGION_CODE",Operator.getRegion());
		  TComboBox cboRegion = (TComboBox)this.getComponent("REGION_CODE");
	        cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(this.
	                getValueString("REGION_CODE")));
         
    	 String sql="SELECT REGION_CODE,ADM_TYPE,DEPT_CODE, " +
    	 		    "DR_CODE1,DR_TEL1,DR_CODE2,DR_TEL2, " +
    	 		    "DR_CODE3,DR_TEL3,DR_CODE4,DR_TEL4, " +
    	 		    "DR_CODE5,DR_TEL5,DR_CODE6,DR_TEL6  " +
    	 		    "FROM INP_SCHDAY ";
         TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
       
		  table=this.getTable("TABLE");
		  table.setParmValue(parm);
		  
    
	}
    
    /**
     * 得到TABLE对象
     * @param tagName
     * @return
     */
     private TTable getTable(String tagName) {
		return (TTable) getComponent(tagName);
	}
   
   /* *//**
     * 电话号改变事件
     *//*
    public boolean onROOMTABLEChargeValue(Object obj) {
        //拿到节点数据,存储当前改变的行号,列号,数据,列名等信息
    	
        TTableNode node = (TTableNode) obj;
        if (node == null)
            return false;
        //如果改变的节点数据和原来的数据相同就不改任何数据
        if (node.getValue().equals(node.getOldValue()))
            return false;
        //拿到table上的parmmap的列名
        String columnName = node.getTable().getDataStoreColumnName(node.
            getColumn());
        
        //拿到table
        TTable table = node.getTable();
        //得到改变的行
        int row = node.getRow();
        //拿到当前改变后的数据
        String value = "" + node.getValue();
        //如果是名称改变了拼音1自动带出,并且科室名称不能为空
        System.out.println("医生的电话号码值"+value);
      if ("DR_CODE2".equals(columnName)) {
            
            //默认简称
            table.setItem(row, "DR_CODE2", value);
            String tel=this.getTle(value);
            table.setItem(row, "DR_TEL2", tel);
            //设置行可保存
            table.getDataStore().setActive(row, true);

        }
      if ("DR_CODE3".equals(columnName)) {
    	  
    	  //默认简称
    	  table.setItem(row, "DR_CODE2", value);
    	  String tel=this.getTle(value);
    	  table.setItem(row, "DR_TEL3", tel);
    	  //设置行可保存
    	  table.getDataStore().setActive(row, true);
    	  
      }
      if ("DR_CODE4".equals(columnName)) {
    	  
    	  //默认简称
    	  table.setItem(row, "DR_CODE2", value);
    	  String tel=this.getTle(value);
    	  table.setItem(row, "DR_TEL4", tel);
    	  //设置行可保存
    	  table.getDataStore().setActive(row, true);
    	  
      }
        return false;
    }*/

    /**
     * 保存
     */
    public void onSave(){
    	
    	String region_code=this.getValueString("REGION_CODE");
    	
    	if("".equals(region_code)){
    		this.messageBox("区域不能为空");
    		this.grabFocus("REGION_CODE");
    		return;
    	}
    	String adm_type=this.getValueString("ADM_TYPE");
    	
    	if("".equals(adm_type)){
    		this.messageBox("门急住别不能为空");
    		this.grabFocus("ADM_TYPE");
    		return;
    	}
    	String dept_code=this.getValueString("DEPT_CODE");
    	if("".equals(dept_code)){
    		this.messageBox("科别不能为空");
    		this.grabFocus("DEPT_CODE");
    		return;
    	 }
    	 String dr_code=this.getValueString("DR_CODE1");
    	 if("".equals(dr_code)){
     		this.messageBox("医生不能为空");
     		this.grabFocus("DR_CODE1");
     		return;
     	 }
    	 //保存之前验证这条数据库中是否已经存在
    	 String sqlq="SELECT REGION_CODE,ADM_TYPE,DEPT_CODE,DR_CODE1,DR_TEL1,DR_CODE2, " +
    	 		     "DR_TEL2,DR_CODE3,DR_TEL3,DR_CODE4,DR_TEL4 FROM INP_SCHDAY "+
		             " WHERE REGION_CODE='"+getValue("REGION_CODE")+"' AND ADM_TYPE='"+getValue("ADM_TYPE")+"'"+
		             " AND DEPT_CODE='"+getValue("DEPT_CODE")+"' AND DR_CODE1='"+getValue("DR_CODE1")+"'";

         TParm parmq = new TParm(TJDODBTool.getInstance().select(sqlq));
		    if (parmq.getErrCode() < 0) {
			this.messageBox("查询错误");
			return;
            }
		   if(parmq.getCount()>0){
			  //this.messageBox("数据库中已经存在此条记录");
			//  return ;
			   // int row=table.getSelectedRow();
				//TParm tparm = table.getParmValue().getRow(row);
				
				
				
			   String sqlw="UPDATE INP_SCHDAY SET DR_TEL1='"+this.getValue("DR_TEL1")+"', " +
			   		       "DR_CODE2='"+this.getValue("DR_CODE2")+"',DR_TEL2='"+this.getValue("DR_TEL2")+"', " +
			   		       "DR_CODE3='"+this.getValue("DR_CODE3")+"',DR_TEL3='"+this.getValue("DR_TEL3")+"', " +
			   		       "DR_CODE4='"+this.getValue("DR_CODE4")+"',DR_TEL4='"+this.getValue("DR_TEL4")+"', " +
			   		       "DR_CODE5='"+this.getValue("DR_CODE5")+"',DR_TEL5='"+this.getValue("DR_TEL5")+"', " +
			   		       "DR_CODE6='"+this.getValue("DR_CODE6")+"',DR_TEL6='"+this.getValue("DR_TEL6")+"', " +
			   		       "OPT_DATE=SYSDATE,OPT_TERM='"+Operator.getIP()+"',OPT_USER='"+Operator.getID()+"' "+
			               "WHERE " +
			               "REGION_CODE='"+this.getValue("REGION_CODE")+"' AND " +
			               "ADM_TYPE='"+this.getValue("ADM_TYPE")+"' AND " +
			               "DEPT_CODE='"+this.getValue("DEPT_CODE")+"' AND " +
			               "DR_CODE1='"+this.getValue("DR_CODE1")+"'";
			         
			          TParm dparm = new TParm(TJDODBTool.getInstance().update(sqlw));
			          if(dparm.getErrCode()<0){
			     		 this.messageBox("修改失败");
			     		 this.onClear();
			     		 return ;
			     	 }
			     	
			     		 this.messageBox("修改成功");
			     		 this.onClear();
			     	     this.onInit();
		   }else{
			     //String sqlt="SELECT TEL1 FROM SYS_OPERATOR WHERE USER_ID='"+this.getValueString("DR_CODE1")+"'";//查找默认医生1的电话
			   ///  TParm parmt = new TParm(TJDODBTool.getInstance().select(sqlt));
		    	 String sql="INSERT INTO INP_SCHDAY (REGION_CODE,ADM_TYPE,DEPT_CODE," +
		    	 		    "DR_CODE1,DR_TEL1,DR_CODE2,DR_TEL2,DR_CODE3," +
		    	 		    "DR_TEL3,DR_CODE4,DR_TEL4,DR_CODE5,DR_TEL5,DR_CODE6,DR_TEL6,OPT_DATE,OPT_TERM,OPT_USER) "+
		    	            "VALUES('"+region_code+"','"+adm_type+"','"+dept_code+"','"+dr_code+"','"+	this.getValueString("DR_TEL1")+"', " +
		    	 		    "'"+this.getValueString("DR_CODE2")+"','"+this.getValueString("DR_TEL2")+"', "+
		    	 		    "'"+this.getValueString("DR_CODE3")+"','"+this.getValueString("DR_TEL3")+"', "+
		    	 		    "'"+this.getValueString("DR_CODE4")+"','"+this.getValueString("DR_TEL4")+"', "+
		    	 		    "'"+this.getValueString("DR_CODE5")+"','"+this.getValueString("DR_TEL5")+"', "+
		    	 		    "'"+this.getValueString("DR_CODE6")+"','"+this.getValueString("DR_TEL6")+"', "+
		    	            " SYSDATE,'"+Operator.getIP()+"','"+Operator.getID()+"')";
		    	 TParm parm = new TParm(TJDODBTool.getInstance().update(sql));
		    	 if(parm.getErrCode()<0){
		    		 this.messageBox("保存失败");
		    		 this.onClear();
		    		 return ;
		    	 }
		    	
		    		 this.messageBox("保存成功");
		    		 this.onClear();
		    	     this.onInit();
				 }
    	 
	}
    /**
     * 电话号点击事件
     */
    public void getDtel(){
    	if(!"".equals(getValueString("DR_CODE1"))){//取得单医生1电话
          this.setValue("DR_TEL1", this.getTel(getValueString("DR_CODE1")));
    	}
    	if(!"".equals(getValueString("DR_CODE2"))){//取得单医2生电话
    		this.setValue("DR_TEL2", this.getTel(getValueString("DR_CODE2")));
    	}
    	if(!"".equals(getValueString("DR_CODE3"))){//取得单医生3电话
    		this.setValue("DR_TEL3", this.getTel(getValueString("DR_CODE3")));
    	}
    	if(!"".equals(getValueString("DR_CODE4"))){//取得单医生4电话
    		this.setValue("DR_TEL4", this.getTel(getValueString("DR_CODE4")));
    	}
    	if(!"".equals(getValueString("DR_CODE5"))){//取得单医生5电话
    		this.setValue("DR_TEL5", this.getTel(getValueString("DR_CODE5")));
    	}
    	if(!"".equals(getValueString("DR_CODE6"))){//取得单医生6电话
    		this.setValue("DR_TEL6", this.getTel(getValueString("DR_CODE6")));
    	}
    	
    		
    }
    /**
     * 查询医生所对应的电话号码
     */
    public String getTel(String value){
    	
    	if(!"".equals(value)){
    		String sqlt="SELECT TEL1 FROM SYS_OPERATOR WHERE USER_ID='"+value+"'";
        	TParm parmt = new TParm(TJDODBTool.getInstance().select(sqlt));
        	return parmt.getValue("TEL1", 0)!=null?parmt.getValue("TEL1", 0):"";
    	}else{
    		return "";
    	}
    	
    	
    }
    /**
     * 查询数据
     */
	public void onQuery() {
    	/*if("".equals(getValueString("REGION_CODE"))){
    		this.messageBox("区域不能为空");
    		this.grabFocus("REGION_CODE");
    		return;
    	}
    	if("".equals(getValueString("ADM_TYPE"))){
    		this.messageBox("门急住别不能为空");
    		this.grabFocus("ADM_TYPE");
    		return;
    	}
    	if("".equals(getValueString("DEPT_CODE"))){
    		this.messageBox("科别不能为空");
    		this.grabFocus("DEPT_CODE");
    		return;
    	 }
    	 if("".equals(getValueString("DR_CODE1"))){
     		this.messageBox("医生不能为空");
     		this.grabFocus("DR_CODE1");
     		return;
     	 }*/
    	 
		String sql="SELECT REGION_CODE, ADM_TYPE, DEPT_CODE, DR_CODE1,DR_TEL1,DR_CODE2,"+
    	           "DR_TEL2,DR_CODE3,DR_TEL3,DR_CODE4,DR_TEL4,DR_CODE5,DR_TEL5,DR_CODE6,DR_TEL6 "+
    	           "FROM INP_SCHDAY "+
    	           "WHERE REGION_CODE='"+getValue("REGION_CODE")+"' ";
		
		if(!"".equals(getValueString("ADM_TYPE"))){
			sql+="AND ADM_TYPE='"+getValue("ADM_TYPE")+"' ";
		}
		
		if(!"".equals(getValueString("DEPT_CODE"))){
			sql+="AND DEPT_CODE='"+getValue("DEPT_CODE")+"' ";
		}
		
		if(!"".equals(getValueString("DR_CODE1"))){
			sql+="AND DR_CODE1='"+getValue("DR_CODE1")+"'";
		}
		           TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
				    if (parm.getErrCode() < 0) {
					this.messageBox("查询错误");
					return;
		           }
				   if(parm.getCount()<=0){
					   this.messageBox("没有查询数据");
					   this.onClear();
					   return ;
				   }
				      table=this.getTable("TABLE");
				      table.setParmValue(parm);
	}
	/**
	 * *增加对Table的监听
	 */
	public void onTABLEClicked(int row){
	 TParm tparm = table.getParmValue().getRow(row);
	  this.setValue("REGION_CODE", tparm.getValue("REGION_CODE"));
	  this.setValue("ADM_TYPE",  tparm.getValue("ADM_TYPE"));
	  this.setValue("DEPT_CODE", tparm.getValue("DEPT_CODE"));
	  this.setValue("DR_CODE1",  tparm.getValue("DR_CODE1"));
	  this.setValue("DR_TEL1",   !"".equals(tparm.getValue("DR_TEL1"))?tparm.getValue("DR_TEL1"):"");
	  this.setValue("DR_CODE2",  tparm.getValue("DR_CODE2"));
	  this.setValue("DR_TEL2",   !"".equals(tparm.getValue("DR_TEL2"))?tparm.getValue("DR_TEL2"):"");
	  this.setValue("DR_CODE3",  tparm.getValue("DR_CODE3"));
	  this.setValue("DR_TEL3",   !"".equals(tparm.getValue("DR_TEL3"))?tparm.getValue("DR_TEL3"):"");
	  this.setValue("DR_CODE4",  tparm.getValue("DR_CODE4"));
	  this.setValue("DR_TEL4",   !"".equals(tparm.getValue("DR_TEL4"))?tparm.getValue("DR_TEL4"):"");
	  this.setValue("DR_CODE5",  tparm.getValue("DR_CODE5"));
	  this.setValue("DR_TEL5",   !"".equals(tparm.getValue("DR_TEL5"))?tparm.getValue("DR_TEL5"):"");
	  this.setValue("DR_CODE6",  tparm.getValue("DR_CODE6"));
	  this.setValue("DR_TEL6",   !"".equals(tparm.getValue("DR_TEL6"))?tparm.getValue("DR_TEL6"):"");
	  this.onLock(false);
	}
	/**
	 * 锁控件
	 */
	public void onLock(boolean flag){
		    callFunction("UI|REGION_CODE|setEnabled", flag);//锁区域
			this.grabFocus("REGION_CODE");
		    callFunction("UI|ADM_TYPE|setEnabled", flag);//锁门急住别
			this.grabFocus("ADM_TYPE");
			callFunction("UI|DEPT_CODE|setEnabled", flag);//锁科室
			this.grabFocus("DEPT_CODE");
			callFunction("UI|DR_CODE1|setEnabled", flag);//锁医生1
			this.grabFocus("DR_CODE1");
	}
	/**
	 * 清空
	 */
	public void onClear() {
		String clearString="REGION_CODE;ADM_TYPE;DEPT_CODE;DR_CODE1;" +
				            "DR_TEL1;DR_CODE2;DR_TEL2;DR_CODE3;DR_TEL3;DR_CODE4;DR_TEL4;DR_CODE5;DR_TEL5;DR_CODE6;DR_TEL6;";
		clearValue(clearString);
		table.removeRowAll();
		this.setValue("REGION_CODE",Operator.getRegion());
		this.onLock(true);
	}
	/**
	 * 删除数据
	 */
	
	public void onDelete(){
		TParm parmM = table.getParmValue();
		if (parmM.getCount() <= 0) {
			this.messageBox("没有保存的数据");
			return;
		}
		int row=table.getSelectedRow();
		
		if (row < 0) {
			this.messageBox("请选择要删除的数据");
			return;
		}
		if (this.messageBox("是否删除", "确认要删除吗？", 2) == 0) {
			TParm tparm = table.getParmValue().getRow(row);
		    String sql="DELETE  FROM INP_SCHDAY "+
		               "WHERE REGION_CODE='"+tparm.getValue("REGION_CODE")+"' AND ADM_TYPE='"+tparm.getValue("ADM_TYPE")+"' "+
		               "AND DEPT_CODE='"+tparm.getValue("DEPT_CODE")+"' AND DR_CODE1='"+tparm.getValue("DR_CODE1")+"'";
		TParm parm = new TParm(TJDODBTool.getInstance().update(sql));
		    if(parm.getErrCode()<0){
		    this.messageBox("删除失败");
		    
		    return;
		    }
		    this.messageBox("删除成功");
		    this.onClear();
		    onInit();
	}
	}
}
