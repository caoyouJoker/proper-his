package com.javahis.ui.inp;

import java.sql.Timestamp;

import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.javahis.util.ExportExcelUtil;

/**
*
* <p>Title: 会诊次数统计表</p>
*
* <p>Description: 会诊次数统计表</p>
*
* <p>Copyright: Copyright (c) caoyong 20139016</p>
*
* <p>Company: JavaHis</p>
*
* @author caoyong
* @version 1.0
*/
public class INP_ConsNumberControl extends TControl {
	private TTable table;
    private TParm result = new TParm();
	
	public void setResult(TParm result){
		this.result=result;
		
	}

	public TParm getResult(){
		return result;
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
		 callFunction("UI|TABLE|addEventListener","TABLE->"+TTableEvent.CLICKED,this,"onTABLEClicked");//单击事件
		 this.setValue("REGION_CODE", Operator.getRegion());
		 
		 TComboBox cboRegion = (TComboBox)this.getComponent("REGION_CODE");
	        cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(this.
	                getValueString("REGION_CODE")));
		 
		// String sql="SELECT REGION_CODE,DEPT_CODE,DR_CODE,CASE_NO,PATH_NAME,SUMNUMBER FROM INP_CONS " ;
		 //TParm parm=new TParm();
	    	//this.initPage();
	        
	    }
	/**
	 * 查询数据
	 */
	public void onQuery(){
		table=this.getTable("TABLE");
		if(table.getRowCount()>0){
			table.removeRowAll();
		}
		/*if("".equals(this.getValueString("REGION_CODE"))){
			this.messageBox("请输入查询区域");
			this.grabFocus("REGION_CODE");
			return;
		}
		if("".equals(this.getValueString("DEPT_CODE"))){
			this.messageBox("请输入查询科室");
			this.grabFocus("DEPT_CODE");
			return;
		}
		if("".equals(this.getValueString("DR_CODE"))){
			this.messageBox("请输入查询医生");
			this.grabFocus("DR_CODE");
			return;
		}*/
	 String sql="SELECT A.REGION_CODE,A.ACCEPT_DEPT_CODE,A.ACCEPT_DR_CODE,"+
	            "A.CASE_NO,B.IPD_NO AS IND_NO,C.PAT_NAME AS PATH_NAME "+
	            "FROM INP_CONS A,ADM_INP B,SYS_PATINFO C " +
	            "WHERE "+
	            "A.REGION_CODE='"+this.getValueString("REGION_CODE")+"' ";
	 
	 if(!"".equals(this.getValueString("DEPT_CODE"))&&this.getValueString("DEPT_CODE")!=null){//科室查询
			sql+=" AND A.ACCEPT_DEPT_CODE='"+this.getValueString("DEPT_CODE")+"' ";
		}
	
	  if(!"".equals(this.getValueString("DR_CODE"))&&this.getValueString("DR_CODE")!=null){//医生查询
			sql+=" AND A.ACCEPT_DR_CODE='"+this.getValueString("DR_CODE")+"'";
		}
	        sql+=" AND A.CASE_NO=B.CASE_NO AND B.MR_NO=C.MR_NO ORDER BY A.CASE_NO ";
	      TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
	      if(parm.getErrCode()<0){
	    	  this.messageBox("查询出现问题");
	    	  return ;
	      }
	      if(parm.getCount()<=0){
	    	  this.messageBox("没有查询数据");
	    	  return;
	      }
	    	 int count=0;
	    	 String regionCode=parm.getValue("REGION_CODE",0);//区域
	    	 String deptCode=parm.getValue("ACCEPT_DEPT_CODE",0);//科室
	    	 String drCode=parm.getValue("ACCEPT_DR_CODE",0);//;医生
	    	 String pathName=parm.getValue("PATH_NAME",0);//患者名称
	    	 String caseNO=parm.getValue("CASE_NO",0);//就诊号
	    	 String indNO=parm.getValue("IND_NO",0);//住院号
	    	 
	    	 for(int i=0;i<parm.getCount("REGION_CODE");i++){//累计病患会诊次数
	    		 if(caseNO.equals(parm.getValue("CASE_NO",i))){
	    			 count++;
	    		 }else{
	    			 this.addResutl(regionCode,deptCode,drCode,indNO,pathName,count);
	    			  count=0;
	    			  caseNO=parm.getValue("CASE_NO",i);
	    			  regionCode=parm.getValue("REGION_CODE",i);
	    	    	  deptCode=parm.getValue("ACCEPT_DEPT_CODE",i);
	    	    	  drCode=parm.getValue("ACCEPT_DR_CODE",i);
	    	    	  indNO=parm.getValue("IND_NO",i);
	    	    	  pathName=parm.getValue("PATH_NAME",i);
	    			  if(caseNO.equals(parm.getValue("CASE_NO",i))){
	    			     count ++;
	    			  }
	    		    }
	    		 if(i==parm.getCount("REGION_CODE")-1){
	    			 this.addResutl(regionCode,deptCode,drCode,indNO,pathName,count); 
		    	  }
	    	     }
	    	 //统计总计
	    	      result.addData("REGION_CODE", "统计总计");
	 		      result.addData("DEPT_CODE", "");
	 		      result.addData("DR_CODE", "");
	 		      result.addData("IND_NO", "");
	 		      result.addData("PATH_NAME", "");
 		          result.addData("SUMNUMBER", parm.getCount());
	              table=this.getTable("TABLE");
	              table.setParmValue(this.getResult());
	      
	}
	/**
	 * 单击事件
	 */
	public void onTABLEClicked(int row){
		 TParm tparm = table.getParmValue().getRow(row);
		 this.setValue("REGION_CODE",tparm.getValue("REGION_CODE"));
		 this.setValue("DEPT_CODE",tparm.getValue("DEPT_CODE"));
		 this.setValue("DR_CODE",tparm.getValue("DR_CODE"));
	}
	/**
	 * 统计会诊次数
	 * @param regionCode
	 * @param deptCode
	 * @param drCode
	 * @param caseNO
	 * @param pathName
	 * @param count
	 */
	public void addResutl(String regionCode,String deptCode, String drCode,String indNo,String pathName, int count ){
		result.addData("REGION_CODE", regionCode);
		result.addData("DEPT_CODE", deptCode);
		result.addData("DR_CODE", drCode);
		result.addData("IND_NO", indNo);
		result.addData("PATH_NAME", pathName);
		result.addData("SUMNUMBER", count);
	}
	/**
	 * 清空
	 */
	public void onClear() {
		String clearString="REGION_CODE;DEPT_CODE;DR_CODE";
		clearValue(clearString);
		this.onInit();
		table.removeRowAll();
	   
		
	}
	/**
	 * 打印
	 */
	public void onPrint() {
		
		if(table.getRowCount()<=0){
			this.messageBox("没有打印数据");
			return ;
		}
		Timestamp today = SystemTool.getInstance().getDate();
			TParm parm = new TParm();
			//TParm tableParm = table.getParmValue();
			TParm  tableParm = table.getShowParmValue();
			// 打印数据
			TParm date = new TParm();
			date.setData("title", "TEXT", "会诊次数统计报表");
			date.setData("user","TEXT",Operator.getName());
            date.setData("Unit","TEXT",tableParm.getValue("DEPT_CODE",0));			
            date.setData("Doctor","TEXT",tableParm.getValue("DR_CODE",0));			
            date.setData("date","TEXT",today.toString().substring(0,today.toString().lastIndexOf(".") ));			
			
			for (int i = 0; i < table.getRowCount(); i++) {
				parm.addData("REGION_CODE", tableParm.getValue("REGION_CODE", i));
				parm.addData("DEPT_CODE", tableParm.getValue("DEPT_CODE", i));
				parm.addData("DR_CODE", tableParm.getValue("DR_CODE", i));
				parm.addData("IND_NO", tableParm.getValue("IND_NO", i));
				parm.addData("PATH_NAME", tableParm.getValue("PATH_NAME", i));
				parm.addData("SUMNUMBER", tableParm.getValue("SUMNUMBER",i));

			}
			
				parm.setCount(parm.getCount("DEPT_CODE"));
				parm.addData("SYSTEM", "COLUMNS", "REGION_CODE");//区域
				parm.addData("SYSTEM", "COLUMNS", "DEPT_CODE");//科室
				parm.addData("SYSTEM", "COLUMNS", "DR_CODE");//医生
				parm.addData("SYSTEM", "COLUMNS", "IND_NO");//住院号
				parm.addData("SYSTEM", "COLUMNS", "PATH_NAME");//病患姓名
				parm.addData("SYSTEM", "COLUMNS", "SUMNUMBER");//累计次数
				date.setData("TABLE", parm.getData());
				this.openPrintWindow("%ROOT%\\config\\prt\\INP\\INP_ConsNumber.jhw", date);
			
	}
	
	/**
     * 汇出Excel
     */
    public void onExport() {
    	if(table.getRowCount()<=0){
    		this.messageBox("没有汇出数据");
    		return;
    	}
        ExportExcelUtil.getInstance().exportExcel(table, "会诊次数统计表");
    }

}
