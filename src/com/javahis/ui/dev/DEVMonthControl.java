package com.javahis.ui.dev;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Vector;



import jdo.dev.DevMMTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;

/**
 * <p>Title: �豸�½�(�½��������)</p> 
 * 
 * <p>Description:�豸�½�(�½��������)</p>
 * 
 * <p>Copyright: Copyright (c) 20130808</p>
 * 
 * <p>Company: ProperSoft </p>
 *      
 * @author  fux 
 * 
 * @version 4.0     
 */
public class DEVMonthControl extends TControl{
	String startDate ="";
	String endDate ="";
	  /**
       * ��ʼ�� 
       */
	    public void onInit() { 
	       messageBox("��ʼ�� ");
		   initPage(); 
	  } 
	  /**   
	   * ��ʼ��ҳ��
	   */       
	  public void initPage() {
	    	String now = StringTool.getString(SystemTool.getInstance().getDate(),
			"yyyyMMdd");
	        this.setValue("DATE", StringTool.getTimestamp(now ,
			"yyyyMMdd"));
	  }
	  /**
	   * ȷ���ս� 
	   */
	  public void onMonth(){
		    messageBox("ȷ���սῪʼ");
//			String date = StringTool.getString(TypeTool
//					.getTimestamp(getValue("MONTH_DATE")), "yyyy-MM");
			String date = getValue("MONTH_DATE").toString();
			messageBox("date"+date);  
			String yearAndMonth[] = date.split("-"); 
			this.messageBox("::::"+date.trim().length()); 
		    if(date.trim().length()<=0){
		        this.messageBox("�·ݲ���Ϊ�գ�");
		        return;  
		      }         
            if (messageBox("ȷ��Ҫ����"+date.substring(0,4)+"��"+date.substring(4,6)+"��"+"���½����β�����","��Ϣ",this.YES_NO_OPTION) != 0)
                return; 
            if(this.getValueString("DEV_CLASS") == null && "".equals(this.getValueString("DEV_CLASS"))){
                this.messageBox("��ѡ������һ���豸����");
                return;  
              }  
            //���ڵ���
            //��ȡĳ��ĳ�µĵ�һ�졾�꣺yearAndMonth[0]  �£�yearAndMonth[1]��
		    startDate = this.getFirstDayOfMonth(Integer.parseInt(yearAndMonth[0]),
		    		Integer.parseInt(yearAndMonth[1])-1);
		    endDate = this.getLastDayOfMonth(Integer.parseInt(yearAndMonth[0]),
		    		Integer.parseInt(yearAndMonth[1])-1);
		    System.out.println("startDate=="+startDate);
		    System.out.println("endDate=="+endDate);
		    messageBox("startDate"+startDate);
		    messageBox("endDate"+endDate);  
		    //����������YYYYMM
			int Mdate1 = Integer.parseInt(yearAndMonth[0]);
			int Mdate2 = Integer.parseInt(yearAndMonth[1])-1;
			System.out.println("Mdate1"+Mdate1);
			System.out.println("Mdate2"+Mdate2); 
            int Mdate = 201309;  
		    //��ת�·�         
		    TParm parm = new TParm();    
		    parm.setData("YYYYMM",Mdate);    
		    parm.setData("START_DATE",startDate); 
		    parm.setData("END_DATE",endDate);  
		    //parm.setData("DEV_CLASS",this.getValueString("DEV_CLASS"));
		    System.out.println("====parm===="+parm);  
		    if(this.saveMonthData(parm)){ 
		        this.messageBox("����ɹ���");
		      }else{
		        this.messageBox("����ʧ�ܣ�");
		      }
	  } 
	  /**
	   * �����½����ݷ���
	   * @param parm TParm 
	   * @return boolean
	   */
	  public boolean saveMonthData(TParm parm){
		  messageBox("�����½����ݷ���");
	    //��ѯ�������(���SQL)
		TParm devData = this.getDevMonthRData(parm);
	    if(devData.getErrCode()<0){
	      return false; 
	    } 
	    System.out.println("result :"+devData);
	    return true;
	  }
	   
	  /**
	   * ����½ᱨ���ʼ������(���SQL)
	   * @return TParm 
	   */
	  public TParm getDevMonthRData(TParm parm){ 
	    TParm result = new TParm();  
	    System.out.println("queryDevMMStockCount"+DevMMTool.getInstance().queryDevMMStockCount(parm).getDouble("COUNT"));
	    if(DevMMTool.getInstance().queryDevMMStockCount(parm).getDouble("COUNT") > 0){
	      result.setErr(-1,"���½�����");     
	      this.messageBox("�����������½ᣡ"); 
	      return result; 
	    }  
	    //�½��������---------����
	    TParm devRkData = DevMMTool.getInstance().queryDevMonthRKData(parm);
	    if(devRkData.getErrCode()<0){
	      result.setErr(-1,"�½��������-->>>��������");
	      return result;  
	    }
	    System.out.println("����ʼ�����ݣ�"+devRkData); 
	    //�½�����������  
	    TParm devCKTRData = DevMMTool.getInstance().queryDevMonthCKTRData(parm);
	    if(devCKTRData.getErrCode()<0){
	      result.setErr(-1,"�½�����������-->>>��������");
	      return result;
	    }                           
	    System.out.println("��������ʼ�����ݣ�"+devCKTRData);
	    //�½�����������
	    TParm devCKTCData = DevMMTool.getInstance().queryDevMonthCKTCData(parm);
	    if(devCKTCData.getErrCode()<0){
	      result.setErr(-1,"�½�����������-->>>��������");
	      return result; 
	    } 
	    System.out.println("���������ʼ�����ݣ�"+devCKTRData);
	    //�½��˻�����-------------DEV_REGRESSGOODS
//	    TParm devTHData = DevMMTool.getInstance().queryDevMonthTHData(parm);
//	    if(devTHData.getErrCode()<0){
//	      result.setErr(-1,"�½��˻�����-->>>��������");
//	      return result; 
//	    }
//	    System.out.println("�˻���ʼ�����ݣ�"+devTHData);
//	    //��ĳ�ʼ��---------------DEV_WASTE
//	    TParm devSHData = DevMMTool.getInstance().queryDevMonthSHData(parm);
//	    if (devSHData.getErrCode() < 0) {
//	      result.setErr( -1, "��ĳ�ʼ��-->>>��������");
//	      return result;
//	    }  
//	    System.out.println("��ĳ�ʼ�����ݣ�" + devSHData);
//	    //�̵��ʼ�� -----------DEV_QTYCHECK
//	    TParm devPDData = DevMMTool.getInstance().queryDevMonthPDData(parm);
//	    if (devPDData.getErrCode() < 0) {
//	      result.setErr( -1, "�̵��ʼ��-->>>��������");
//	      return result;
//	    }
//	    System.out.println("�̵��ʼ�����ݣ�" + devPDData);
	    
	    TParm MMParm = new TParm(); 
	    //��� 
	    MMParm.setData("INWAREHOUSE",devRkData);
	    //���� 
	    MMParm.setData("GIFTIN",devCKTRData);      
	    //����  
	    MMParm.setData("GIFTOUT",devCKTCData); 
	    System.out.println("MMParm"+MMParm);
//	    //�˻� 
//	    MMParm.setData("REGRESSGOODS",devTHData);
//	    //���
//	    MMParm.setData("WASTE",devSHData);
//	    //�̵� 
//	    MMParm.setData("CHECKMODI",devPDData); 
	    //����ʱ���(�³�����ĩ)��ѯ��stockd��������   
	    TParm stockDParm = new TParm(); 
	    stockDParm = ParmStockD();
	    System.out.println("stockDParm"+stockDParm); 
	    for(int i = 0;i<stockDParm.getCount("DEV_CODE"); i++){ 
	    	 //forѭ����������    
	    	 System.out.println("i"+i);  
		     TParm  MMresult = DevMMTool.getInstance().insertMMStock(MMParm);
		     System.out.println("MMresult"+MMresult);  
	    }  
	    return result;  
	  } 
	  /**
	   * ��ѯ��STOCKD��������    
	   * @return TParm  
	   */    
	  public TParm ParmStockD(){ 
		String sql = " SELECT * FROM DEV_STOCKD " +  
				     " WHERE INWAREHOUSE_DATE BETWEEN " +
				     " TO_DATE('"+startDate+"','YYYYMMDD') " +
				     " AND TO_DATE('"+endDate+"','YYYYMMDD') "; 
		System.out.println("sql=="+sql); 
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;	     
	  }  
	  /**  
	   * ����½�����
	   * @return String
	   */ 
	  public String getMonthStartDate(String monthData){  
	    if(monthData.trim().length()<=0){
	      return "";
	    }
	    if (monthData.substring(4, 6).equals("01")) {
	      return this.valiDataMonthDate(monthData);
	    }
	    //09 -01 = 08
	    int month = Integer.parseInt(monthData.substring(4, 6)) - 1;
	    String m = String.valueOf(month);
	    if (m.length() == 1) {
	      m = "0" + m;
	    }
	    String result = monthData.substring(0, 4) + m;
	    return result;
	  } 
	
	  /**
	   * ��֤�½�����
	   * @param monthData String
	   * @return String
	   */
	  public String valiDataMonthDate(String monthData){
	    if(monthData.trim().length()<=0){
	      return "";
	    }
	    int year = Integer.parseInt(monthData.substring(0,4))-1;
	    String result = String.valueOf(year)+"12";
	    return result;
	  }
	  /**
	   * ����½����� 
	   * @return String
	   */ 
	  public String getMonthEndDate(String monthData){
	    return monthData;
	  }
	  /**
	   * �༭ͳ���·�
	   * @param dateStr String
	   * @return String
	   */
	  public String getDataValue(String dateStr){ 
	    if(dateStr.trim().length()<=0){
	      return "";
	    }
	    String[] str = dateStr.split("\\/");
	    String strDate = "";
	    for(int i=0;i<str.length;i++){
	      strDate+=str[i];
	    }
	    return strDate;
	  }
	  /**
	   * ��ȡĳ��ĳ�µ����һ��
	   * @param dateStr String
	   * @return String
	   */
	    public static String getLastDayOfMonth(int year, int month) {     
	        Calendar cal = Calendar.getInstance();     
	        cal.set(Calendar.YEAR, year);     
	        cal.set(Calendar.MONTH, month);     
	        cal.set(Calendar.DAY_OF_MONTH,cal.getActualMaximum(Calendar.DATE));  
	       return  new   SimpleDateFormat( "yyyyMMdd ").format(cal.getTime());  
	    }    
		  /**
		   * ��ȡĳ��ĳ�µĵ�һ��
		   * @param dateStr String
		   * @return String
		   */
	    public static String getFirstDayOfMonth(int year, int month) {     
	        Calendar cal = Calendar.getInstance();     
	        cal.set(Calendar.YEAR, year);     
	        cal.set(Calendar.MONTH, month);  
	        cal.set(Calendar.DAY_OF_MONTH,cal.getMinimum(Calendar.DATE));  
	       return   new   SimpleDateFormat( "yyyyMMdd ").format(cal.getTime());  
	    }   
}
