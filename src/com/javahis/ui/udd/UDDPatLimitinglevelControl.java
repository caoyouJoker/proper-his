package com.javahis.ui.udd;
import java.sql.Timestamp; 
import java.text.DecimalFormat;

import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;
import jdo.udd.UDDPatLimitinglevelTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable; 
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;


/**
 * <p>
 * Title: 门/急/住使用限制级以上抗菌药物所用的患者比例统计
 * </p>
 * 
 * <p>
 * Description: 门/急/住使用限制级以上抗菌药物所用的患者比例统计
 * </p>
 * 
 * <p>
 * Copyright: Bluecore
 * 20140212
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author caoy
 * @version 1.0
 */
public class UDDPatLimitinglevelControl extends TControl {
	private TTable table_i;// 住院表
	private TTable table_oe;// 门急诊表
	private TTable table_oes;// 门急诊表---人数
	private int pageFlg = 0;// 页签标记（0：住院；1：门急诊）

	/**
	 * 初始化方法
	 */
	public void onInit() {
		// 初始化时间控件
		Timestamp date = SystemTool.getInstance().getDate();
		this.setValue("S_DATE", date.toString().substring(0, 10).replace("-",
				"/")
				+ " 00:00:00");
		this.setValue("E_DATE", date.toString().substring(0, 10).replace("-",
				"/")
				+ " 23:59:59");
		this.table_i = this.getTable("TABLE_I");// 得到住院表格
		this.table_oe = this.getTable("TABLE_OE");// 得到门急诊表格
		table_oes = this.getTable("TABLE_OES");// 得到门急诊表格---人数
		// 初始化院区
		setValue("REGION_CODE", Operator.getRegion());
		TComboBox cboRegion = (TComboBox) this.getComponent("REGION_CODE");
		cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(
				this.getValueString("REGION_CODE")));
		setValue("OPT_USER", Operator.getID());// 初始化操作者
		this.clearValue("ODI_TYPE;DEPT_CODE;DR_CODE;ADM_TYPE;DR_OE_CODE");
		this.setValue("ODI_TYPE", "2");
		this.callFunction("UI|ODI_TYPE|setEnabled", false);
	}

	/**
	 * 获取表格组件
	 * 
	 * @param tagName
	 * @return
	 */
	private TTable getTable(String tagName) {
		return (TTable) this.getComponent(tagName);
	}

	/**
	 * 页签点击事件
	 */
	public void onChangeTTabbedPane() {
		TTabbedPane tabbedPane = ((TTabbedPane) this
				.getComponent("tTabbedPane_2"));
		if (tabbedPane.getSelectedIndex() == 0) {
			pageFlg = 0;// 住院
		} else if (tabbedPane.getSelectedIndex() == 1) {
			pageFlg = 1;// 门急诊
		} else {
			pageFlg = 2;// 门急诊--人数
		}
	}

	/**
	 * 查询
	 */
	public void onQuery() {
		TParm parm=new TParm();
		TParm reultTotnum=new TParm();
		TParm resultantnum=new TParm();
		DecimalFormat dec=new DecimalFormat("##.##%");
		String sDate = StringTool.getString(TypeTool.getTimestamp(getValue("S_DATE")), "yyyyMMddHHmmss");
		String eDate = StringTool.getString(TypeTool.getTimestamp(getValue("E_DATE")), "yyyyMMddHHmmss");
				parm.setData("S_DATE",sDate);
				parm.setData("E_DATE",eDate);
				parm.setData("DR_CODE",this.getValueString("DR_CODE"));
				parm.setData("DEPT_CODE",this.getValueString("DEPT_CODE"));
				
		
		
		TTabbedPane tabbedPane = ((TTabbedPane) this
				.getComponent("tTabbedPane_2"));
		if (tabbedPane.getSelectedIndex() == 0) {
			
			reultTotnum=UDDPatLimitinglevelTool.getInstance().getSelectTotnum(parm);
			if(reultTotnum.getErrCode()<0){
				this.messageBox("查询出现出问题");
				return;
			}
			
			resultantnum=UDDPatLimitinglevelTool.getInstance().getselectAntnum(parm);
			
			if(resultantnum.getErrCode()<0){
				this.messageBox("查询出现出问题");
				return;
			}
			int antNum=0;//制级以上人数
			/** 合并计算限制级以上人数 **/
			for(int i=0;i<reultTotnum.getCount();i++){
				boolean flag=false;
				for(int j=0;j<resultantnum.getCount();j++){
					if(reultTotnum.getValue("DEPT_CODE",i).equals(resultantnum.getValue("DEPT_CODE",j))&&
							reultTotnum.getValue("VS_DR_CODE",i).equals(resultantnum.getValue("VS_DR_CODE",j))){
						    antNum=resultantnum.getInt("ANTI_NUM",j);
						    flag=true;
						    break;
					}
				}
				
				if(flag){
					 reultTotnum.addData("ANTI_NUM", antNum)	;
				}else{
					 reultTotnum.addData("ANTI_NUM", 0)	;
				}
				
				
			}
			/** 计算限制级以上人数比例 **/
			double antiRate=0;//比例
			double aNum=0;//限已上人数
			double tNum=0;//总人数
			for(int a=0;a<reultTotnum.getCount();a++){
				aNum=reultTotnum.getDouble("ANTI_NUM",a);
				tNum=reultTotnum.getDouble("TOT_NUM",a);
				if(tNum!=0){
					antiRate=aNum/tNum;
				}
				reultTotnum.addData("ANTI_RATE", dec.format(antiRate));
			}
			
			/** 添加小计 **/
			String deptCode=reultTotnum.getValue("DEPT_CODE", 0);
			double totnum=0;
			double antnum=0;
			double antirate=0;
			TParm sparm=new TParm();
			for(int k=0;k<reultTotnum.getCount();k++){
				if(deptCode.equals(reultTotnum.getValue("DEPT_CODE", k))){
					sparm.addRowData(reultTotnum, k);
					totnum+=reultTotnum.getDouble("TOT_NUM", k);
					antnum+=reultTotnum.getDouble("ANTI_NUM", k);
				}else{
					deptCode=reultTotnum.getValue("DEPT_CODE", k);
					sparm.addData("REGION_CODE", "小计:");
					sparm.addData("ODI_TYPE",reultTotnum.getValue("ODI_TYPE", k-1) );
					sparm.addData("DEPT_CODE", reultTotnum.getValue("DEPT_CODE", k-1));
					sparm.addData("VS_DR_CODE", "");
					sparm.addData("TOT_NUM", totnum);
					sparm.addData("ANTI_NUM", antnum);
					sparm.addData("ANTI_RATE", totnum!=0?dec.format(antnum/totnum):dec.format(antirate));
					 totnum=0;
					 antnum=0;
					 antirate=0;
					 if(deptCode.equals(reultTotnum.getValue("DEPT_CODE", k))){
						    sparm.addRowData(reultTotnum, k);
						    totnum+=reultTotnum.getDouble("TOT_NUM", k);
							antnum+=reultTotnum.getDouble("ANTI_NUM", k);
					 }
				}
				if(k==reultTotnum.getCount()-1){
					sparm.addData("REGION_CODE", "小计:");
					sparm.addData("ODI_TYPE",reultTotnum.getValue("ODI_TYPE", k-1) );
					sparm.addData("DEPT_CODE", reultTotnum.getValue("DEPT_CODE", k-1));
					sparm.addData("VS_DR_CODE", "");
					sparm.addData("TOT_NUM", totnum);
					sparm.addData("ANTI_NUM", antnum);
					sparm.addData("ANTI_RATE", totnum!=0?dec.format(antnum/totnum):dec.format(antirate));
				}
				
				
			}
			table_i.setParmValue(sparm);
			
			
			
			
		} 

	}
	
	/**
	 * 汇出Excel
	 */
	public void onExport() {
		// 得到UI对应控件对象的方法（UI|XXTag|getThis）
		try {
			TTabbedPane tabbedPane = ((TTabbedPane) this
					.getComponent("tTabbedPane_2"));
			if (tabbedPane.getSelectedIndex() == 0) {
				if (table_i.getParmValue().getCount()<=0) {
					messageBox("没有可导入的数据！");
					return;
				}
				ExportExcelUtil.getInstance().exportExcel(table_i,
						"住院患者抗菌药物处方比例统计");
			} else if (tabbedPane.getSelectedIndex() == 1) {
				if (table_oe.getParmValue().getCount()<=0) {
					messageBox("没有可导入的数据！");
					return;
				}
				ExportExcelUtil.getInstance().exportExcel(table_oe,
						"门急诊患者抗菌药物处方比例统计-处方");
			}else{
				if (table_oes.getParmValue().getCount()<=0) {
					messageBox("没有可导入的数据！");
					return;
				}
				ExportExcelUtil.getInstance().exportExcel(table_oes,
				"门急诊患者抗菌药物处方比例统计-人数");
			}

		} catch (NullPointerException e) {
			// TODO: handle exception
			messageBox("没有可导入的数据！");
			return;
		}
	}

	/**
	 * 清空方法
	 */
	public void onClear() {
		Timestamp date = SystemTool.getInstance().getDate();
		// 设置时间控件的值
		this.setValue("S_DATE", date.toString().substring(0, 10).replace("-",
				"/")
				+ " 00:00:00");
		this.setValue("E_DATE", date.toString().substring(0, 10).replace("-",
				"/")
				+ " 23:59:59");
		// 清除表格中的数据
		table_i.setParmValue(new TParm());
		table_oe.setParmValue(new TParm());
		table_oes.setParmValue(new TParm());
		// 清除其他控件的值
		this.clearValue("OPT_USER;ODI_TYPE;DEPT_CODE;DR_CODE;ADM_TYPE;DR_OE_CODE");
		this.setValue("ODI_TYPE", "2");
	}
}
