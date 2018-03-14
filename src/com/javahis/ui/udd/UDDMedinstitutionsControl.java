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
 * <p>Title: �����ҽ�ƻ�������ҩ���ٴ�Ӧ����Ϣ��</p>
 *
 * <p>Description:�����ҽ�ƻ�������ҩ���ٴ�Ӧ����Ϣ��</p>
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
	 * ��ʼ��
	 */
	public void init() {
		table=this.getTable("TABLE");
		initPage();
	}
	
	private TTable getTable(String tagName) {
		return (TTable) getComponent(tagName);
	}
	 /**
	 * ��ѯ
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
			
			//========��ѯÿ�����ݵ�ֵ======= start========
			//��������
			TParm oResult=UDDMedinstitutionsTool.getInstance().getSelectOdate(parm);
			//����ʹ�ÿ���ҩ����
			TParm oAResult=UDDMedinstitutionsTool.getInstance().getSelectOAdate(parm);
			//��������
			TParm iResult=UDDMedinstitutionsTool.getInstance().getSelectEdate(parm);
			//����ʹ�ÿ�������
			TParm iAResult=UDDMedinstitutionsTool.getInstance().getSelectEAdate(parm);
			//��Ժ����
			TParm outResult=UDDMedinstitutionsTool.getInstance().getSelectoutdate(parm);
			//��Ժʹ�ÿ���ҩ����
			TParm outaResult=UDDMedinstitutionsTool.getInstance().getSelectoutAdate(parm);
			//��Ժ����סԺ������
			TParm outDaysResult=UDDMedinstitutionsTool.getInstance().getselectoutDays(parm);
			//��Ժ��������ʹ�ü�����ҩ��ʹ������
			TParm outAspeNumResult=UDDMedinstitutionsTool.getInstance().getselectoutSpeanum(parm);
			//I���п���������
			TParm InumResult=UDDMedinstitutionsTool.getInstance().getselectInum(parm);
			//II���п���������
			TParm IInumResult=UDDMedinstitutionsTool.getInstance().getselectIInum(parm);
			//��Ժ����������ʹ�����Ƽ�����ҩ������
			TParm outLimitnumResult=UDDMedinstitutionsTool.getInstance().getselectoutLimitnum(parm);
			//��Ժ����������ʹ�����⼶����ҩ������
			TParm outspicialnumResult=UDDMedinstitutionsTool.getInstance().getselectoutSpeacialnum(parm);
			//��Ժ���߿���ҩ������ʹ��ǰ΢�����ͼ�������
			TParm outamicnumResult=UDDMedinstitutionsTool.getInstance().getselectoutAmicnum(parm);
			//סԺʹ�ÿ���ҩ����
			TParm inanumResult=UDDMedinstitutionsTool.getInstance().getselectinAnum(parm);
			//סԺʹ�ÿ���ҩ����ʹ��ǰ΢�����ͼ�������
			TParm inamicnumResult=UDDMedinstitutionsTool.getInstance().getselectinAmicnum(parm);
			//סԺ������
			TParm inNumResult=UDDMedinstitutionsTool.getInstance().getselectinNum(parm);
			//סԺ����������ʹ�����Ƽ�����ҩ������
			TParm InLimitnum=UDDMedinstitutionsTool.getInstance().getselectInLimitnum(parm);
			//סԺ����������ʹ�����Ƽ�����ҩ��ʹ��ǰ΢�����ͼ�������
			TParm InAmiLimitnum=UDDMedinstitutionsTool.getInstance().getselectInAmiLimitnum(parm);
			//סԺ����������ʹ�����⼶����ҩ������
			TParm InSpeacialnum=UDDMedinstitutionsTool.getInstance().getselectInSpeacialnum(parm);
			//סԺ����������ʹ�����⼶����ҩ��ʹ��ǰ΢�����ͼ�������
			TParm InSpeacialAminum=UDDMedinstitutionsTool.getInstance().getselectInSpeacialAminum(parm);
			
			//=======================��ѯÿ�����ݵ�ֵ====== end===========
			
			
			
			//========��ѯÿ�����ݵ�ֵ�ϲ�һ��TParm======= start========
			
			//����ʹ�ÿ���ҩ���������������ϲ�
			result=this.getResult(oResult, oAResult, "O_ANUM");
			//��Ժ������
			result=this.getResult(result, outResult, "OUT_NUM");
			//��Ժ����ʹ�ÿ���ҩ������
			result=this.getResult(result, outaResult, "OUT_ANUM");
			//��������
			result=this.getResult(result, iResult, "E_NUM");
			//����ʹ�ÿ�������
			result=this.getResult(result, iAResult, "E_ANUM");
			//��Ժ����סԺ������
			result=this.getResult(result, outDaysResult, "OUT_DAYS");
			//��Ժ��������ʹ�ü�����ҩ��ʹ������
			result=this.getResult(result, outAspeNumResult, "OUT_SPECIALANUM");
			//I���п���������
			result=this.getResult(result, InumResult, "I_NUM");
			//II���п���������
			result=this.getResult(result, IInumResult, "II_OPENUM");
			//��Ժ����������ʹ�����Ƽ�����ҩ������
			result=this.getResult(result, outLimitnumResult, "OUT_LIMITANUM");
			//��Ժ����������ʹ�����⼶����ҩ������
			result=this.getResult(result, outspicialnumResult, "OUT_SPEANUM");
			//��Ժ���߿���ҩ������ʹ��ǰ΢�����ͼ�������
			result=this.getResult(result, outamicnumResult, "OUT_AMICNUM");
			//��Ժ���߿���ҩ������ʹ��ǰ΢�����ͼ�������
			result=this.getResult(result, inanumResult, "IN_ANUM");
			//��Ժ���߿���ҩ������ʹ��ǰ΢�����ͼ�������
			result=this.getResult(result, inamicnumResult, "IN_AMICNUM");
			//��Ժ���߿���ҩ������ʹ��ǰ΢�����ͼ�������
			result=this.getResult(result, inNumResult, "IN_NUM");
			//סԺ����������ʹ�����Ƽ�����ҩ������
			result=this.getResult(result, InLimitnum, "IN_LIMITANUM");
			//סԺ����������ʹ�����Ƽ�����ҩ��ʹ��ǰ΢�����ͼ�������
			result=this.getResult(result, InAmiLimitnum, "IN_AMILIMITANUM");
			//סԺ����������ʹ�����⼶����ҩ������
			result=this.getResult(result, InSpeacialnum, "IN_SPEANUM");
			//סԺ����������ʹ�����⼶����ҩ��ʹ��ǰ΢�����ͼ�������
			result=this.getResult(result, InSpeacialAminum, "IN_AMISPEANUM");

			//========��ѯÿ�����ݵ�ֵ�ϲ�һ��TParm======= end========;
			
			String sdate="";
			for(int i=0;i<result.getCount("SDATE");i++){
				sdate=result.getValue("SDATE",i).substring(0,4)+"��"+result.getValue("SDATE",i).substring(4,6)+"��";
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
	 * �ϲ�ÿһ�еĲ�ѯ����
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
	 * ��%����average
	 */
	
	public TParm getResult(TParm parm){
		DecimalFormat format=new DecimalFormat("##.##%");
		DecimalFormat df=new DecimalFormat("##.#");
		//TParm result=new TParm();
		//���￹��ҩ��ʹ����(%)
		double orate=0.0;
		//���￹��ҩ��ʹ����(%)
		double erate=0.0;
		//��Ժ����ƽ��סԺ������
		double outaverageday=0;
		//סԺ���߿���ҩ������ʹ��ǰ΢�����ͼ���(%)
		double inamicrate=0.0;
		//סԺ���߿���ҩ��ʹ���ʣ�%��
		double inarate=0.0;
		//סԺ�������Ƽ�����ҩ������ʹ��ǰ΢�����ͼ���(%)
		double inlimitamirate=0.0;
		//סԺ�������⼶����ҩ������ʹ��ǰ΢�����ͼ���(%)
		double inspeamirate=0.0;




		for(int i=0;i<parm.getCount("SDATE");i++){
			//���￹��ҩ��ʹ����(%)
			if(parm.getDouble("O_NUM", i)!=0){
				orate=parm.getDouble("O_ANUM", i)/parm.getDouble("O_NUM", i);
			}else{
				orate=0.0;
			}
			//���￹��ҩ��ʹ����(%)
			if(parm.getDouble("E_NUM", i)!=0){
				erate=parm.getDouble("E_ANUM", i)/parm.getDouble("E_NUM", i);
			}else{
				erate=0.0;
			}
			//��Ժ����ƽ��סԺ������
			if(parm.getDouble("OUT_NUM", i)!=0){
				outaverageday=parm.getDouble("OUT_DAYS", i)/parm.getDouble("OUT_NUM", i);
			}else{
				outaverageday=0;
			}
			//סԺ���߿���ҩ������ʹ��ǰ΢�����ͼ���(%)

			if(parm.getDouble("IN_ANUM", i)!=0){
				inamicrate=parm.getDouble("IN_AMICNUM", i)/parm.getDouble("IN_ANUM", i);
			}else{
				inamicrate=0.0;
			}
			//סԺ���߿���ҩ��ʹ���ʣ�%��

			if(parm.getDouble("IN_NUM", i)!=0){
				inarate=parm.getDouble("IN_ANUM", i)/parm.getDouble("IN_NUM", i);
			}else{
				inarate=0.0;
			}
			//סԺ�������Ƽ�����ҩ������ʹ��ǰ΢�����ͼ���(%)

			if(parm.getDouble("IN_LIMITANUM", i)!=0){
				inlimitamirate=parm.getDouble("IN_AMILIMITANUM", i)/parm.getDouble("IN_LIMITANUM", i);
			}else{
				inlimitamirate=0.0;
			}
			//סԺ�������⼶����ҩ������ʹ��ǰ΢�����ͼ���(%)
			
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
	 * ��ѯʱ��θ�ֵ
	 */
	public void initPage() {

	    Timestamp date = StringTool.getTimestamp(new Date());
		// ʱ����Ϊ1��
		// ��ʼ����ѯ����
		this.setValue("E_DATE", date.toString().substring(0, 7).replace('-','/'));
		this.setValue("S_DATE", StringTool.rollDate(date, -32).toString().substring(0, 7).replace('-','/'));
	}
	
	/**
     * ���Excel
     */
    public void onExport() {
    	if(table.getRowCount()<=0){
    		this.messageBox("û�л������");
    		return;
    	}
        ExportExcelUtil.getInstance().exportExcel(table, "�����ҽ�ƻ�������ҩ���ٴ�Ӧ����Ϣ��");
    }
}
