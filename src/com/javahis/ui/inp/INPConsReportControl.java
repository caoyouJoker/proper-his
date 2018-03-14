package com.javahis.ui.inp;

import java.sql.Timestamp;
import java.util.Date;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
//import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.OdiUtil;
/**
 * 
 * <p>
 * Title: 会诊报告
 * </p>
 * 
 * <p>
 * Description: 会诊报告
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) caoyong 20139023
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author caoyong
 * @version 1.0
 */
public class INPConsReportControl extends TControl{

	private TTable table;
	private TTable table1;
	/**
	 * 病案号
	 */
	private String mrno;
	/**
	 * 就诊号
	 */
	private String caseno;
	/**
	 * 住院日期
	 */
	private Timestamp admDate;
	/**
	 * 住院号
	 */
	private String indcode;
	/**
	 * 调用会诊申请单标记：会诊申请为false，会诊报告为true
	 * yanjing，20131101
	 */
	private boolean flg = true;
	/**
	 * 开单医生
	 */
	private  String DR_CODE=" AND DR_CODE='"+Operator.getID()+"' ";
	/**
	 * 被会值班医生
	 */
	private  String ACCEPT_DR_CODE=" AND ACCEPT_DR_CODE= '"+Operator.getID()+"' ";
	/**
	 * 被会指定医生
	 */
	private  String ASSIGN_DR_CODE=" AND ASSIGN_DR_CODE= '"+Operator.getID()+"' ";
	/**
	 * 初始化查询数据倒序排列
	 */
	private  String CONSDATEDESC=" ORDER BY CONS_DATE DESC";
	/**
	 * sql常量
	 */
	private  String sql="SELECT REPORT_FLG,URGENT_FLG,DR_DEPT,DR_CODE,CONS_DATE,RECIPIENT_DATE,ACCEPT_DR_CODE," +
	                   " REPORT_DATE,REPORT_DR_CODE ,CONS_CODE,CASE_NO FROM INP_CONS  " +
	                   " WHERE " +
                       " CONCEL_CAUSE_CODE IS NULL " ;
	
	public Timestamp getAdmDate() {
		return admDate;
	}
	public void setAdmDate(Timestamp admDate) {
		this.admDate = admDate;
	}
	public String getMrno() {
		return mrno;
	}
	public void setMrno(String mrno) {
		this.mrno = mrno;
	}
	public String getCaseno() {
		return caseno;
	}
	public void setCaseno(String caseno) {
		this.caseno = caseno;
	}
	
	public boolean getFlg() {
		return flg;
	}
	
	public void setFlg(boolean flg) {
		this.flg = flg;
	}
	
	public String getIndcode() {
		return indcode;
	}
	public void setIndcode(String indcode) {
		this.indcode = indcode;
	}
	

	
	/**
	 * 得到TABLE对象
	 * @param tagName
	 * @return
	 */
	 private TTable getTable(String tagName) {
			return (TTable) getComponent(tagName);
		}
	 /**
	  * 初始化数据
	  */
	 public void onInit(){
		 callFunction("UI|TABLE|addEventListener","TABLE->"+TTableEvent.CLICKED,this,"onTABLEClicked");//接受会诊清单单击事件
		 callFunction("UI|TABLE1|addEventListener","TABLE1->"+TTableEvent.CLICKED,this,"onTABLEClickeq");//提出会诊清单点击事件
	     this.initPage();//初始化参数赋值
	     onQueryall();
	        
	    }
	 /**
		 *初始化
		 */
	private void initPage() {

			 table = getTable("TABLE");
			 table1 = getTable("TABLE1");
			//给查询时间段赋值
			 Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance().getDate(), -1);
	         Timestamp today = SystemTool.getInstance().getDate();
	         this.setValue("S_DATE", yesterday);//查询起时间
	         this.setValue("E_DATE", today);//查询迄时间
	         this.setValue("KIND_CODE1", "01");
	         this.setValue("DR_CODE1", Operator.getID());
	         this.setValue("DEPT_CODE1", Operator.getDept());
	         Timestamp date = StringTool.getTimestamp(new Date());
	         this.setValue("REPORT_DATE",date.toString().substring(0,10).replace('-', '/'));//初始化开单时间
	      
		}
	/**
	 * 初始化全部查询
	 */
	public void onQueryall(){
		
	      TParm parm = new TParm(TJDODBTool.getInstance().select(sql+ACCEPT_DR_CODE+CONSDATEDESC));//被会被值班医生
	      TParm parm1 = new TParm(TJDODBTool.getInstance().select(sql+DR_CODE+CONSDATEDESC));//开单医生
	      TParm parm2 = new TParm(TJDODBTool.getInstance().select(sql+ASSIGN_DR_CODE+CONSDATEDESC));//被会指定医生
	      if(parm.getCount()>0&&parm2.getCount()>0){
	    	  parm.addParm(parm2);
	      }
	      if(parm.getCount()<=0&&parm2.getCount()>0){
	    	  parm=parm2;
	      }
	     for(int i=0;i<parm.getCount("CONS_DATE");i++){
	    	  parm.setData("REPORT_FLG",i,"Y".equals(parm.getValue("REPORT_FLG",i))?"完成":"未完成");//状态编号传化为其所对应的汉字
			 }
	     for(int i=0;i<parm1.getCount("CONS_DATE");i++){
	    	  parm1.setData("REPORT_FLG",i,"Y".equals(parm.getValue("REPORT_FLG",i))?"完成":"未完成");//状态编号传化为其所对应的汉字
			 }
	    	  table = getTable("TABLE");
	    	  table.setParmValue(parm);
	    	  table1 = getTable("TABLE1");
	    	  table1.setParmValue(parm1);
	    	 
	}
	
	/**
	 * 查询数据
	 */
	public void onQuery(){
		 onClearon();
		 String startTime = StringTool.getString(TypeTool.getTimestamp(getValue(
         "S_DATE")), "yyyyMMdd");//查询起时间
         String endTime = StringTool.getString(TypeTool.getTimestamp(getValue(
         "E_DATE")), "yyyyMMdd");//查询迄时间
         String sqlq= " AND CONS_DATE BETWEEN  TO_DATE ('"+startTime+"','YYYYMMDDHH24MISS') AND "+
                      " TO_DATE ('"+endTime+"','YYYYMMDDHH24MISS')"; 
         
	 String sql="SELECT REPORT_FLG,URGENT_FLG,DR_DEPT,DR_CODE,CONS_DATE,RECIPIENT_DATE,ACCEPT_DR_CODE," +
	 		    " REPORT_DATE,REPORT_DR_CODE ,CONS_CODE,CASE_NO FROM INP_CONS  " +
	 		    "WHERE " +
	 		    "CONS_DATE BETWEEN " +
	 		    "TO_DATE ('"+startTime+"','YYYYMMDDHH24MISS') AND "+
                "TO_DATE ('"+endTime+"','YYYYMMDDHH24MISS')   AND " +
                "CONCEL_CAUSE_CODE IS NULL" ;
	
	
	 String sql1="";
	 String sql2="";
	 String sql3="";
	 String sqla="";
	 if(this.getValueString("KIND_CODE1").length()>0){//会诊种类
			sqla+=" AND KIND_CODE='"+this.getValueString("KIND_CODE1")+"' ";
		}
	 if(this.getValueString("REPORT_FLG1").length()>0){//完成
		    sqla+=" AND REPORT_FLG='"+this.getValueString("REPORT_FLG1")+"' ";
	 }
	 if(this.getValueString("DEPT_CODE1").length()<=0&&this.getValueString("DR_CODE1").length()>0){
		    sqla+=" AND ASSIGN_DR_CODE='"+this.getValueString("DR_CODE1")+"' ";
		 
	 }
	
	//开单科室医生
	  if(this.getValueString("DEPT_CODE1").length()>0){// 会诊开单科室
			sql1+=" AND DR_DEPT='"+this.getValueString("DEPT_CODE1")+"'";
		}
	  if(this.getValueString("DR_CODE1").length()>0){//医生查询
		    sql1+=" AND DR_CODE='"+this.getValueString("DR_CODE1")+"'";
	  }
	  
	  //被会科室医生
	 if(this.getValueString("DEPT_CODE1").length()>0){// 会诊开单科室
			sql2+=" AND ACCEPT_DEPT_CODE='"+this.getValueString("DEPT_CODE1")+"'";
			}
	 if(this.getValueString("DR_CODE1").length()>0){//医生查询
			sql2+=" AND ACCEPT_DR_CODE='"+this.getValueString("DR_CODE1")+"'";
		  }
	 //指定科室医生
	 if(this.getValueString("DEPT_CODE1").length()>0){// 会诊开单科室
			sql3+=" AND ASSEPT_DEPT_CODE='"+this.getValueString("DEPT_CODE1")+"'";
			}
	 if(this.getValueString("DR_CODE1").length()>0){//医生查询
			sql3+=" AND ASSIGN_DR_CODE='"+this.getValueString("DR_CODE1")+"'";
		  }
	 
		  String sqld=sql+sqlq+sqla+sql2;
		  String sqle=sql+sqlq+sqla+sql1;
		  String sqlf=sql+sqlq+sqla+sql3;
	      TParm parm = new TParm(TJDODBTool.getInstance().select(sqld));//被会值班医生
	      TParm parm1 = new TParm(TJDODBTool.getInstance().select(sqle));//开单医生
	      TParm parm2 = new TParm(TJDODBTool.getInstance().select(sqlf));//被会指定医生
	      
	      if(parm.getCount()>0&&parm2.getCount()>0){//合并被会值班医生和被会指定医生
	    	  parm.addParm(parm2);
	      }
	      if(parm.getCount()<=0&&parm2.getCount()>0){//背会指定医生
	    	  parm=parm2;
	      }
	     
	     if(parm.getErrCode()<0&&parm1.getErrCode()<0){
	    	  this.messageBox("查询出现问题");
	    	  return ;
	      }
	      if(parm.getCount()<=0&&parm1.getCount()<=0){
	    	  this.messageBox("没有查询数据");
	    	  this.onClear();
	    	  return;
	      }
	      
	     for(int i=0;i<parm.getCount("CONS_DATE");i++){
	    	  parm.setData("REPORT_FLG",i,"Y".equals(parm.getValue("REPORT_FLG",i))?"完成":"未完成");//状态编号传化为其所对应的汉字
			 }
	     for(int i=0;i<parm1.getCount("CONS_DATE");i++){
	    	  parm1.setData("REPORT_FLG",i,"Y".equals(parm.getValue("REPORT_FLG",i))?"完成":"未完成");//状态编号传化为其所对应的汉字
			 }
	     // if(!flag){
	    	  table = getTable("TABLE");
	    	  table.setParmValue(parm);
	      //}else{
	    	  table1 = getTable("TABLE1");
	    	  table1.setParmValue(parm1);
	      // } 
	      
	      
	       
	}
	/**
	 * 保存
	 */
	 public void onSave(){
	    	
	    	String conscode=this.getValueString("CONS_CODE");
	    	if("".equals(conscode)){
	    		this.messageBox("会诊单号不能为空");
	    		this.grabFocus("CONS_CODE");
	    		return;
	    	}
		
	    	
	     String startTime = StringTool.getString(TypeTool.getTimestamp(getValue(
	         "REPORT_DATE")), "yyyy/MM/dd");//
		   String sql="UPDATE INP_CONS SET " +
		   "REPORT_DEPT_CODE='"+this.getValueString("REPORT_DEPT_CODE")+"', " +
		   //yanjing 添加收件日期显示    20131113  start
		   "RECIPIENT_DATE=to_date('"+startTime+"','yyyy/mm/dd')," +
		   //yanjing 添加收件日期显示    20131113  end
		   "REPORT_DR_CODE='"+this.getValueString("REPORT_DR_CODE")+"', " +
		   "REPORT_DATE=to_date('"+startTime+"','yyyy/mm/dd')," +
		   "DESCRIPTION='"+this.getValue("DESCRIPTION")+"',REPORT_FLG='"+this.getValueString("REPORT_FLG")+"' "+
		   "WHERE "+
		   "CONS_CODE='"+this.getValue("CONS_CODE")+"'";
				          TParm dparm = new TParm(TJDODBTool.getInstance().update(sql));
				          if(dparm.getErrCode()<0){
				     		 this.messageBox("保存失败");
				     		 //this.onClear();
				     		 return ;
				     	 }
				     	
				     		 this.messageBox("保存成功");
				     		 //this.onClear();
				     	     //this.onInit();
				     		  this.onQuery();
			   }
			    	
	public void onTABLEClickeq(int row){
		TParm tparm= new TParm();
			    tparm = table1.getParmValue().getRow(row);
			    callFunction("UI|REPORT_DEPT_CODE|setEnabled", false);
				callFunction("UI|REPORT_DR_CODE|setEnabled", false);
				callFunction("UI|REPORT_DATE|setEnabled", false);
				callFunction("UI|REPORT_FLG|setEnabled", false);
				callFunction("UI|save|setEnabled", false);
				this.getMessage(tparm);
	}
	/**
	 * 单击事件
	 */
	public void onTABLEClicked(int row){
		if(row<0){
			return;
		}
		TParm tparm= new TParm();
		
			     tparm = table.getParmValue().getRow(row);
			     callFunction("UI|REPORT_DEPT_CODE|setEnabled", true);
				 callFunction("UI|REPORT_DR_CODE|setEnabled", true);
				 callFunction("UI|REPORT_DATE|setEnabled", true);
				 callFunction("UI|REPORT_FLG|setEnabled", true);
				 callFunction("UI|save|setEnabled", true);
				 
				 this.getMessage(tparm);
		  
	}
	
	
	/**
	 * //病患资料 ,会诊单资料
	 * @param tparm
	 */
	public void getMessage(TParm tparm){
				String sqlq="SELECT A.MR_NO,A.IPD_NO,A.DEPT_CODE,A.BED_NO," +
			    "A.IN_DATE,A.STATION_CODE,A.VS_DR_CODE,B.PAT_NAME, " +
			    "B.BIRTH_DATE,B.SEX_CODE  "+
			    "FROM " +
			    "ADM_INP A,SYS_PATINFO B "+
			    "WHERE "+
			    "A.MR_NO=B.MR_NO(+) AND " +
			    "A.CASE_NO='"+tparm.getValue("CASE_NO")+"'";
				TParm parmq = new TParm(TJDODBTool.getInstance().select(sqlq));
				//会诊单资料
				String sql="SELECT KIND_CODE,CONS_CODE,ADM_TYPE,URGENT_FLG,CONS_DATE,DR_CODE,DR_TEL," +
						    "REPORT_DEPT_CODE,REPORT_DR_CODE,REPORT_DATE,DESCRIPTION,REPORT_FLG "+
						    "FROM INP_CONS "+
						    "WHERE "+
						    "CONS_CODE='"+tparm.getValue("CONS_CODE")+"'";
						  TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
				if(parm.getErrCode()<0){
				  this.messageBox("查询出现问题");
				  return ;
				}
				if(parm.getCount()<=0){
				  this.messageBox("没有相关数据");
				  return;
				}
				  Timestamp date = StringTool.getTimestamp(new Date());
				 String datime=date.toString().substring(0,10).replace('-', '/');//初始化开单时间
				 
				 Timestamp sysDate = SystemTool.getInstance().getDate();
					// 根据住院日期 和出生日期计算出年龄
						Timestamp temp = parmq.getTimestamp("BIRTH_DATE",0) == null ? sysDate
								: parmq.getTimestamp("BIRTH_DATE", 0);
						// 计算年龄
						String age = "0";
						if (!"".equals(parmq.getTimestamp("IN_DATE", 0))&&parmq.getTimestamp("IN_DATE", 0) != null)
							age = OdiUtil.getInstance().showAge(temp,
									parmq.getTimestamp("IN_DATE", 0));
						else{
							age = "";
						}
				//病患资料
				this.setValue("MR_NO", parmq.getValue("MR_NO",0));//病案号
				this.setValue("IPD_NO", parmq.getValue("IPD_NO",0));//住院号
				this.setValue("PAT_NAME", parmq.getValue("PAT_NAME",0));//姓名
				this.setValue("SEX_CODE", "1".equals(parmq.getValue("SEX_CODE",0)) ? "男" :"女");//性别
				this.setValue("DEPT_CODE", parmq.getValue("DEPT_CODE",0));//科别
				this.setValue("VS_DR_CODE", parmq.getValue("VS_DR_CODE",0));//经治医生
				this.setValue("STATION_CODE", parmq.getValue("STATION_CODE",0));//病区
				this.setValue("BED_NO", parmq.getValue("BED_NO",0));//床号
				this.setValue("IN_DATE", parmq.getValue("IN_DATE",0).substring(0, 10).replace("-", "/"));//住院日期
				this.setValue("AGE", age);//年龄
				//会诊单资料
				this.setValue("KIND_CODE", parm.getValue("KIND_CODE",0));//科室种类
				this.setValue("CONS_CODE", parm.getValue("CONS_CODE",0));//会诊单号
				this.setValue("ADM_TYPE", parm.getValue("ADM_TYPE",0));//门急住别
				this.setValue("URGENT_FLG", parm.getValue("URGENT_FLG",0));//急
				this.setValue("CONS_DATE", tparm.getValue("CONS_DATE").substring(0,10).replace("-","/"));//开单时间
				this.setValue("DR_CODE", parm.getValue("DR_CODE",0));//开单医生
				this.setValue("DR_TEL", parm.getValue("DR_TEL",0));//开单医生电话
				this.setValue("REPORT_DEPT_CODE", !"".equals(parm.getValue("REPORT_DEPT_CODE",0))?parm.getValue("REPORT_DEPT_CODE",0):Operator.getDept());//会诊报告科室
				this.setValue("REPORT_DR_CODE", parm.getValue("REPORT_DR_CODE",0).length()>0?parm.getValue("REPORT_DR_CODE",0):Operator.getID());//会诊报告医生
				this.setValue("REPORT_DATE", !"".equals(parm.getValue("REPORT_DATE",0))?
					 parm.getValue("REPORT_DATE",0).substring(0,10).replace("-","/"):datime);// 报告完成时间
				this.setValue("DESCRIPTION", parm.getValue("DESCRIPTION",0));//会诊报告说明
				this.setValue("REPORT_FLG", parm.getValue("REPORT_FLG",0));//报告完成注记
				this.onLock(false);
				String mrno=parmq.getValue("MR_NO",0);
				String caseno=tparm.getValue("CASE_NO");
				Timestamp admDate= parmq.getTimestamp("IN_DATE",0);
				String indcode=parmq.getValue("IPD_NO",0);
				this.setMrno(mrno);
				this.setCaseno(caseno);
				this.setAdmDate(admDate);
				this.setIndcode(indcode);
				this.setFlg(flg);

	}
	/**
	 * 锁控件
	 */
	public void onLock(boolean flag){
		    callFunction("UI|KIND_CODE|setEnabled", flag);//科室种类
			this.grabFocus("KIND_CODE");
		    callFunction("UI|CONS_CODE|setEnabled", flag);//会诊单号
			this.grabFocus("CONS_CODE");
			callFunction("UI|ADM_TYPE|setEnabled", flag);//门急住别
			this.grabFocus("ADM_TYPE");
			callFunction("UI|URGENT_FLG|setEnabled", flag);//急
			this.grabFocus("URGENT_FLG");
			callFunction("UI|CONS_DATE|setEnabled", flag);//开单时间
			this.grabFocus("CONS_DATE");
			callFunction("UI|DR_CODE|setEnabled", flag);//开单医生
			this.grabFocus("DR_CODE");
			callFunction("UI|DR_TEL|setEnabled", flag);//开单电话
			this.grabFocus("DR_TEL");
			
	}
	/**
	 * 清空
	 */
	
	public void onClear() {
		String clearString="ADM_TYPE;KIND_CODE;CONS_CODE;URGENT_FLG;CONS_DATE;DR_CODE;DR_TEL;REPORT_FLG1;" +
		 		           "REPORT_DEPT_CODE;REPORT_DR_CODE;REPORT_DATE;DESCRIPTION;REPORT_FLG;"+
		 		           "MR_NO;IPD_NO;PAT_NAME;SEX_CODE;AGE;DEPT_CODE;STATION_CODE;BED_NO;VS_DR_CODE;IN_DATE";
		clearValue(clearString);
		this.onInit();
		table.removeRowAll();
		table1.removeRowAll();
		this.onLock(true);
	   
		
	}
	/**
	 * 会诊报告
	 */
	
	public void onConsReport(){
		int row = table.getSelectedRow();
		int row1 = table1.getSelectedRow();
		if(row<0&&row1<0){
			this.messageBox("请选择一条会诊清单数据");
			return;
		}
		
		if(row>=0){
			onInpDepApp(this.getFlg(),this.getMrno(),this.getCaseno(),this.getAdmDate(),this.getIndcode());
			return;
		}
		if(row1>=0){
			this.messageBox("提出会诊单不能打开电子病历");
			
			return;
			}
		
		
	}
	/**
	 * 会诊单
	 * 
	 */
	public void onInpDepApp(boolean flg,String mrno,String caseno,Timestamp admDate, String indcode) {
		TParm parm = new TParm();
		parm.setData("FLG", flg);//会诊报告还是会诊申请调用申请单标记
		parm.setData("MR_NO", mrno);
		parm.setData("CASE_NO", caseno);
		parm.setData("ADM_DATE",admDate);
		parm.setData("IPD_NO",indcode);
		this.openWindow("%ROOT%\\config\\inp\\INPDeptApp.x", parm);
	}
	/**
	 * 
	 *//*
	public TParm setparm(){
		TParm result= new TParm();
		result.setData("MR_NO", this.getMrno());
		result.setData("CASE_NO", this.getMrno());
		result.setData("APPROVE_FLG", "Y");// 会诊注记
		result.setData("USE_FLG", "N");
		result.setData("OPT_USER", Operator.getID());
		result.setData("OPT_TERM", Operator.getIP());
		
		return result;
	}*/
	
	/**
	 *查询table清空
	 */
	
	public void onClearon() {
		String clearString="ADM_TYPE;KIND_CODE;CONS_CODE;URGENT_FLG;CONS_DATE;DR_CODE;DR_TEL;REPORT_FLG1; " +
		"REPORT_DEPT_CODE;REPORT_DR_CODE;REPORT_DATE;DESCRIPTION;REPORT_FLG; "+
		"MR_NO;IPD_NO;PAT_NAME;SEX_CODE;AGE;DEPT_CODE;STATION_CODE;BED_NO;VS_DR_CODE;IN_DATE";
		clearValue(clearString);
		table.removeRowAll();
		table1.removeRowAll();
		this.onLock(true);
		
		
	}

}
