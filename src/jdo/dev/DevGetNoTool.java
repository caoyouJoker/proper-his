package jdo.dev;

import java.sql.Timestamp;
import java.util.Calendar;

import jdo.sys.SystemTool;

import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;

/**
 * <p>Title:�ʲ�����</p>
 *
 * <p>Description:�ʲ����� </p>
 *
 * <p>Copyright: Copyright (c) 2013</p>
 *
 * <p>Company:javahis </p>  
 *
 * @author  fux
 * @version 1.0   
 */
public class DevGetNoTool extends TJDOTool{
	/**
	 * ʵ��
	 */ 
	private static DevGetNoTool instanceObject;
	 
	/** 
	 * �õ�ʵ�� 
	 * @return
	 */
	public static DevGetNoTool getInstance() {
		if (null == instanceObject) {
			instanceObject = new DevGetNoTool();
		} 
		return instanceObject;
	}

	//��װ�̶��ʲ����
    /** 
     * ȡ���豸�������豸�������ϼ�����
     * @param devCode String 
     * @return String  
     */   
    public String getDevType(String devCode){  
    	devCode= devCode.substring(0, 5);
        return devCode;   
    }  
	 
    /** 
     * ȡ���豸���������յ�������6λ��
     * @param devCode String  
     * @return String
     */   
    public String getDevYMD(String devCode){  
    	Timestamp date = SystemTool.getInstance().getDate();
    	String year =  date.toString().substring(2, 4);
    	String month =  date.toString().substring(5, 7);
    	String day =  date.toString().substring(8, 10);
    	String Date = date.toString(); 
    	String wel  = year+month+day;
    	//2014-03-11 16:44:59.0  
    	//wel  40-
//    	System.out.println("Date"+Date);
//    	System.out.println("wel"+wel); 
        return wel;     
    } 
      
    
    /** 
     * ȡ���豸���������NUM 
     * @param devCode String  
     * @return String  
     */    
    public String getDevNumber(String devCode){  
        String sql = " SELECT MAX(DEV_CODE_DETAIL) AS DEV_CODE_DETAIL " +
        		" FROM DEV_STOCKDD " +   
        		" WHERE DEV_CODE = '"+devCode+"'" ;
        StringBuffer SQL = new StringBuffer();
        SQL.append(sql);  
        TParm parm = new TParm(TJDODBTool.getInstance().select(SQL.toString()));
        String num = parm.getValue("DEV_CODE_DETAIL",0).toString();    
        return num;         
    } 
     
    /**  
     * ��װ(������װ)
     * @param devCode String 
     * @return TParm    
     */    
    public TParm finshNumber(String devCode,int qty){
    	//dev_code�͵�̨ʽ��  ��Ҫ�����¼���ˮ���dev_detail_code ��ΪΨһ����
    	//83011140514001
    	String numString  = "";
    	if ("".equals(getDevNumber(devCode))) {
			numString = "";   
		}else{
		    numString = getDevNumber(devCode).substring(11,14).replace('[', ' ').replace(']', ' ').trim(); 
		}
        System.out.println("numString::::"+numString);
        String dev1 = getDevType(devCode).replace('[', ' ').replace(']', ' ').trim();  
        String dev2 = getDevYMD(devCode).replace('[', ' ').replace(']', ' ').trim(); 
        String devDetailCode = "";
        
        TParm parm = new TParm();
        //���û�ж�Ĭ��ΪdevCode+����+001
        int	num = 0;
        for (int i = 0; i < qty; i++) {
        	if (i==0) {  
        		//��ʼĬ��Ϊ001 ÿ�ζ��ۼ�
        		 if("".equals(numString)){ 
                 	numString = "001"; 
                 	num = Integer.parseInt(numString);
                 } else{
             	    num = Integer.parseInt(numString)+1;
                 }
			}
        	else{
        		num = Integer.parseInt(numString)+1;
        	}   
        	numString = num + "";
        	int letgh =  numString.length();
            for(int j = 0;j < 3 - letgh ; j++){
                numString = "0" + numString;    
            }     
        	devDetailCode = dev1+dev2+numString;   
        	parm.setData("DEV_CODE_DETAIL",i,devDetailCode); 
        	System.out.println("parmһ����װ:::"+parm);
		} 
        
    	  
    	return parm; 
    }  
    
    
    /**  
     * ��װ(������װ)
     * @param devCode String 
     * @return TParm    
     */    
    public TParm finshNumberSecord(String devCode,int qty,String devcodedetailold){
    	//dev_code�͵�̨ʽ��  ��Ҫ�����¼���ˮ���dev_detail_code ��ΪΨһ����
    	//83011140514001
    	String numString  = "";
		numString =devcodedetailold.substring(11,14).replace('[', ' ').replace(']', ' ').trim(); 
        String devDetailCode = "";
        TParm parm = new TParm();
        //���û�ж�Ĭ��ΪdevCode+����+001  
        int	num = 0;  
        for (int i = 0; i < qty; i++) {
            num = Integer.parseInt(numString)+1;        
        	numString = num + "";
        	int letgh =  numString.length();
            for(int j = 0;j < 3 - letgh ; j++){
                numString = "0" + numString;            
            }       
        	devDetailCode = devcodedetailold.substring(0,11)+numString;   
        	parm.setData("DEV_CODE_DETAIL",i,devDetailCode); 
        	System.out.println("parm������װ:::"+parm);  
		}   
        
    	  
    	return parm; 
    }  

}
