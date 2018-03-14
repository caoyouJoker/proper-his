package jdo.dev;

import java.sql.Timestamp;
import java.util.Calendar;

import jdo.sys.SystemTool;

import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;

/**
 * <p>Title:资产编码</p>
 *
 * <p>Description:资产编码 </p>
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
	 * 实例
	 */ 
	private static DevGetNoTool instanceObject;
	 
	/** 
	 * 得到实例 
	 * @return
	 */
	public static DevGetNoTool getInstance() {
		if (null == instanceObject) {
			instanceObject = new DevGetNoTool();
		} 
		return instanceObject;
	}

	//组装固定资产编号
    /** 
     * 取得设备基本档设备分类码上级分类
     * @param devCode String 
     * @return String  
     */   
    public String getDevType(String devCode){  
    	devCode= devCode.substring(0, 5);
        return devCode;   
    }  
	 
    /** 
     * 取得设备基本档当日的年月日6位码
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
     * 取得设备基本档最大NUM 
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
     * 组装(正常组装)
     * @param devCode String 
     * @return TParm    
     */    
    public TParm finshNumber(String devCode,int qty){
    	//dev_code就到台式机  我要加入下级流水编号dev_detail_code 作为唯一编码
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
        //如果没有都默认为devCode+日期+001
        int	num = 0;
        for (int i = 0; i < qty; i++) {
        	if (i==0) {  
        		//初始默认为001 每次都累加
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
        	System.out.println("parm一次组装:::"+parm);
		} 
        
    	  
    	return parm; 
    }  
    
    
    /**  
     * 组装(二次组装)
     * @param devCode String 
     * @return TParm    
     */    
    public TParm finshNumberSecord(String devCode,int qty,String devcodedetailold){
    	//dev_code就到台式机  我要加入下级流水编号dev_detail_code 作为唯一编码
    	//83011140514001
    	String numString  = "";
		numString =devcodedetailold.substring(11,14).replace('[', ' ').replace(']', ' ').trim(); 
        String devDetailCode = "";
        TParm parm = new TParm();
        //如果没有都默认为devCode+日期+001  
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
        	System.out.println("parm二次组装:::"+parm);  
		}   
        
    	  
    	return parm; 
    }  

}
