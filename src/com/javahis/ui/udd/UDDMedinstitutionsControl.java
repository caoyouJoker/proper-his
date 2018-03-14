package com.javahis.ui.udd;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Date;
import jdo.udd.UDDMedinstitutionsTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;
/**
 * <p>Title: 天津市医疗机构抗菌药物临床应用信息表</p>
 *
 * <p>Description:天津市医疗机构抗菌药物临床应用信息表</p>
 *
 * <p>Copyright: Copyright (c)cao yong 2013</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author 2013.12.11
 * @version 1.0
 */
public class UDDMedinstitutionsControl  extends TControl{
	private TTable table;
	/**
	 * 初始化
	 */
	public void init() {
		table=this.getTable("TABLE");
		initPage();
	}
	
	private TTable getTable(String tagName) {
		return (TTable) getComponent(tagName);
	}
	 /**
	 * 查询
	 */
	public void onQuery() {
		table.removeRowAll();
		TParm parm=new TParm();
		TParm result=new TParm();
		TParm sresult=new TParm();
		
		
			String sDate = StringTool.getString(TypeTool.getTimestamp(getValue("S_DATE")), "yyyyMM");
			String eDate = StringTool.getString(TypeTool.getTimestamp(getValue("E_DATE")), "yyyyMM");
			parm.setData("S_DATE",sDate);
			parm.setData("E_DATE",eDate);
			
			//========查询每列数据的值======= start========
			//门诊人数
			TParm oResult=UDDMedinstitutionsTool.getInstance().getSelectOdate(parm);
			//门诊使用抗菌药人数
			TParm oAResult=UDDMedinstitutionsTool.getInstance().getSelectOAdate(parm);
			//急诊人数
			TParm iResult=UDDMedinstitutionsTool.getInstance().getSelectEdate(parm);
			//急诊使用抗菌人数
			TParm iAResult=UDDMedinstitutionsTool.getInstance().getSelectEAdate(parm);
			//出院人数
			TParm outResult=UDDMedinstitutionsTool.getInstance().getSelectoutdate(parm);
			//出院使用抗菌药人数
			TParm outaResult=UDDMedinstitutionsTool.getInstance().getSelectoutAdate(parm);
			//出院患者住院人天数
			TParm outDaysResult=UDDMedinstitutionsTool.getInstance().getselectoutDays(parm);
			//出院患者特殊使用级抗菌药物使用人数
			TParm outAspeNumResult=UDDMedinstitutionsTool.getInstance().getselectoutSpeanum(parm);
			//I类切口手术例数
			TParm InumResult=UDDMedinstitutionsTool.getInstance().getselectInum(parm);
			//II类切口手术例数
			TParm IInumResult=UDDMedinstitutionsTool.getInstance().getselectIInum(parm);
			//出院患者治疗性使用限制级抗菌药物人数
			TParm outLimitnumResult=UDDMedinstitutionsTool.getInstance().getselectoutLimitnum(parm);
			//出院患者治疗性使用特殊级抗菌药物人数
			TParm outspicialnumResult=UDDMedinstitutionsTool.getInstance().getselectoutSpeacialnum(parm);
			//出院患者抗菌药物治疗使用前微生物送检验人数
			TParm outamicnumResult=UDDMedinstitutionsTool.getInstance().getselectoutAmicnum(parm);
			//住院使用抗菌药物数
			TParm inanumResult=UDDMedinstitutionsTool.getInstance().getselectinAnum(parm);
			//住院使用抗菌药物数使用前微生物送检验人数
			TParm inamicnumResult=UDDMedinstitutionsTool.getInstance().getselectinAmicnum(parm);
			//住院者人数
			TParm inNumResult=UDDMedinstitutionsTool.getInstance().getselectinNum(parm);
			//住院患者治疗性使用限制级抗菌药物人数
			TParm InLimitnum=UDDMedinstitutionsTool.getInstance().getselectInLimitnum(parm);
			//住院患者治疗性使用限制级抗菌药物使用前微生物送检验人数
			TParm InAmiLimitnum=UDDMedinstitutionsTool.getInstance().getselectInAmiLimitnum(parm);
			//住院患者治疗性使用特殊级抗菌药物人数
			TParm InSpeacialnum=UDDMedinstitutionsTool.getInstance().getselectInSpeacialnum(parm);
			//住院患者治疗性使用特殊级抗菌药物使用前微生物送检验人数
			TParm InSpeacialAminum=UDDMedinstitutionsTool.getInstance().getselectInSpeacialAminum(parm);
			
			//=======================查询每列数据的值====== end===========
			
			
			
			//========查询每列数据的值合并一个TParm======= start========
			
			//门诊使用抗菌药人数和门诊人数合并
			result=this.getResult(oResult, oAResult, "O_ANUM");
			//出院人数和
			result=this.getResult(result, outResult, "OUT_NUM");
			//出院人数使用抗菌药物人数
			result=this.getResult(result, outaResult, "OUT_ANUM");
			//急诊人数
			result=this.getResult(result, iResult, "E_NUM");
			//急诊使用抗菌人数
			result=this.getResult(result, iAResult, "E_ANUM");
			//出院患者住院人天数
			result=this.getResult(result, outDaysResult, "OUT_DAYS");
			//出院患者特殊使用级抗菌药物使用人数
			result=this.getResult(result, outAspeNumResult, "OUT_SPECIALANUM");
			//I类切口手术例数
			result=this.getResult(result, InumResult, "I_NUM");
			//II类切口手术例数
			result=this.getResult(result, IInumResult, "II_OPENUM");
			//出院患者治疗性使用限制级抗菌药物人数
			result=this.getResult(result, outLimitnumResult, "OUT_LIMITANUM");
			//出院患者治疗性使用特殊级抗菌药物人数
			result=this.getResult(result, outspicialnumResult, "OUT_SPEANUM");
			//出院患者抗菌药物治疗使用前微生物送检验人数
			result=this.getResult(result, outamicnumResult, "OUT_AMICNUM");
			//出院患者抗菌药物治疗使用前微生物送检验人数
			result=this.getResult(result, inanumResult, "IN_ANUM");
			//出院患者抗菌药物治疗使用前微生物送检验人数
			result=this.getResult(result, inamicnumResult, "IN_AMICNUM");
			//出院患者抗菌药物治疗使用前微生物送检验人数
			result=this.getResult(result, inNumResult, "IN_NUM");
			//住院患者治疗性使用限制级抗菌药物人数
			result=this.getResult(result, InLimitnum, "IN_LIMITANUM");
			//住院患者治疗性使用限制级抗菌药物使用前微生物送检验人数
			result=this.getResult(result, InAmiLimitnum, "IN_AMILIMITANUM");
			//住院患者治疗性使用特殊级抗菌药物人数
			result=this.getResult(result, InSpeacialnum, "IN_SPEANUM");
			//住院患者治疗性使用特殊级抗菌药物使用前微生物送检验人数
			result=this.getResult(result, InSpeacialAminum, "IN_AMISPEANUM");

			//========查询每列数据的值合并一个TParm======= end========;
			
			String sdate="";
			for(int i=0;i<result.getCount("SDATE");i++){
				sdate=result.getValue("SDATE",i).substring(0,4)+"年"+result.getValue("SDATE",i).substring(4,6)+"月";
				result.setData("SDATE",i,sdate);
				//result.addData("IN_ARATE", 0);
				result.addData("OUT_ADDD", 0);
				//result.addData("OUT_DAYS", 0);
				//result.addData("OUT_AVERAGEDAY", 0);
				result.addData("IN_ADDDONEHUNDER", 0);
				//result.addData("OUT_SPECIALANUM", 0);
				result.addData("OUT_SPEDDD", 0);
				result.addData("SPE_DDDONEHUNDERD", 0);
				result.addData("SPE_ARATE", 0);
				//result.addData("I_NUM", 0);
				result.addData("I_ANUM", 0);
				result.addData("I_ARATE", 0);
				result.addData("I_A5RATE", 0);
				result.addData("I_A24RATE", 0);
				result.addData("INT_OPENUM", 0);
				result.addData("INT_OPEANUM", 0);
				result.addData("INT_ATATE", 0);
				//result.addData("II_OPENUM", 0);
				result.addData("II_OPEANUM", 0);
				result.addData("II_OPEANUM", 0);
				result.addData("II_A5RATE", 0);
				result.addData("II_A24TATE", 0);
				//result.addData("OUT_AMICNUM", 0);
				//result.addData("IN_AMICRATE", 0);
				//result.addData("OUT_LIMITANUM", 0);
				//result.addData("IN_LIMITAMICRATE", 0);
				//result.addData("OUT_SPEANUM", 0);
				//result.addData("IN_SPEMICRATE", 0);
				//result.addData("O_RATE", 0);
			    //result.addData("E_RATE", 0);
			}
			
			sresult=this.getResult(result);
			table.setParmValue(sresult);
	}
	/**
	 * 合并每一列的查询数据
	 * @param oResult
	 * @param parm
	 * @param type
	 * @return
	 */
	public TParm getResult(TParm oResult ,TParm parm, String type){
		//TParm result=new TParm();
		String  date="";
		boolean flag=false;
		for(int i=0;i<oResult.getCount();i++){
			flag=false;
			date=oResult.getValue("SDATE",i);
			for(int j=0;j<parm.getCount();j++){
				if(date.equals(parm.getValue("SDATE",j))){
					oResult.addData(type, parm.getValue(type,j));
					flag=true;
					break;
				}
			}
			
		 if(!flag){
			 oResult.addData(type, 0);
		   }
		}
		return oResult;
	}
	
	/**
	 * 求（%）或average
	 */
	
	public TParm getResult(TParm parm){
		DecimalFormat format=new DecimalFormat("##.##%");
		DecimalFormat df=new DecimalFormat("##.#");
		//TParm result=new TParm();
		//门诊抗菌药物使用率(%)
		double orate=0.0;
		//急诊抗菌药物使用率(%)
		double erate=0.0;
		//出院患者平均住院人天数
		double outaverageday=0;
		//住院患者抗菌药物治疗使用前微生物送检率(%)
		double inamicrate=0.0;
		//住院患者抗菌药物使用率（%）
		double inarate=0.0;
		//住院患者限制级抗菌药物治疗使用前微生物送检率(%)
		double inlimitamirate=0.0;
		//住院患者特殊级抗菌药物治疗使用前微生物送检率(%)
		double inspeamirate=0.0;




		for(int i=0;i<parm.getCount("SDATE");i++){
			//门诊抗菌药物使用率(%)
			if(parm.getDouble("O_NUM", i)!=0){
				orate=parm.getDouble("O_ANUM", i)/parm.getDouble("O_NUM", i);
			}else{
				orate=0.0;
			}
			//急诊抗菌药物使用率(%)
			if(parm.getDouble("E_NUM", i)!=0){
				erate=parm.getDouble("E_ANUM", i)/parm.getDouble("E_NUM", i);
			}else{
				erate=0.0;
			}
			//出院患者平均住院人天数
			if(parm.getDouble("OUT_NUM", i)!=0){
				outaverageday=parm.getDouble("OUT_DAYS", i)/parm.getDouble("OUT_NUM", i);
			}else{
				outaverageday=0;
			}
			//住院患者抗菌药物治疗使用前微生物送检率(%)

			if(parm.getDouble("IN_ANUM", i)!=0){
				inamicrate=parm.getDouble("IN_AMICNUM", i)/parm.getDouble("IN_ANUM", i);
			}else{
				inamicrate=0.0;
			}
			//住院患者抗菌药物使用率（%）

			if(parm.getDouble("IN_NUM", i)!=0){
				inarate=parm.getDouble("IN_ANUM", i)/parm.getDouble("IN_NUM", i);
			}else{
				inarate=0.0;
			}
			//住院患者限制级抗菌药物治疗使用前微生物送检率(%)

			if(parm.getDouble("IN_LIMITANUM", i)!=0){
				inlimitamirate=parm.getDouble("IN_AMILIMITANUM", i)/parm.getDouble("IN_LIMITANUM", i);
			}else{
				inlimitamirate=0.0;
			}
			//住院患者特殊级抗菌药物治疗使用前微生物送检率(%)
			
			if(parm.getDouble("IN_SPEANUM", i)!=0){
				inspeamirate=parm.getDouble("IN_AMISPEANUM", i)/parm.getDouble("IN_SPEANUM", i);
			}else{
				inspeamirate=0.0;
			}
			
			//parm.setData("O_RATE", i, format.format(orate));
			//parm.setData("E_RATE", i, format.format(erate));
			//parm.setData("OUT_AVERAGEDAY", i, df.format(outaverageday));
			parm.addData("O_RATE", format.format(orate));
			parm.addData("E_RATE",  format.format(erate));
			parm.addData("OUT_AVERAGEDAY", df.format(outaverageday));
			parm.addData("IN_ARATE", format.format(inarate));
			parm.addData("IN_AMICRATE", format.format(inamicrate));
			parm.addData("IN_LIMITAMICRATE", format.format(inlimitamirate));
			parm.addData("IN_SPEMICRATE", format.format(inspeamirate));
			
		}
		return parm;
		
	}
	
	/**
	 * 查询时间段赋值
	 */
	public void initPage() {

	    Timestamp date = StringTool.getTimestamp(new Date());
		// 时间间隔为1天
		// 初始化查询区间
		this.setValue("E_DATE", date.toString().substring(0, 7).replace('-','/'));
		this.setValue("S_DATE", StringTool.rollDate(date, -32).toString().substring(0, 7).replace('-','/'));
	}
	
	/**
     * 汇出Excel
     */
    public void onExport() {
    	if(table.getRowCount()<=0){
    		this.messageBox("没有汇出数据");
    		return;
    	}
        ExportExcelUtil.getInstance().exportExcel(table, "天津市医疗机构抗菌药物临床应用信息表");
    }
}
