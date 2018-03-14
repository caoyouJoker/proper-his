package action.spc;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import jdo.spc.SPCOpdOrderPathTool;
import jdo.sys.SystemTool;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import action.spc.opdOrderClient.SPCOpdOrderSynchWsTool;

import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;
import com.dongyang.patch.Patch;

/**
 * <p>
 * Title: OPD_ORDER表 物联网与his同步
 * </p>
 * 
 * <p>
 * Description: OPD_ORDER表 物联网与his同步
 * </p>
 * @author liuzhen 2013.1.18
 * @version 1.0
 */

public class SPCOpdOrderPatch extends Patch {

	private static  TConfig getProp() {
        TConfig config = TConfig.getConfig("WEB-INF\\config\\system\\TConfig.x");
        return config;
	}
 
	 public  static String getWebServicesIp(){
		 TConfig config = getProp() ;
		 String url = config.getString("", "WEB_SERVICES_58IP");
		 return url;
	 }

	
	
	public boolean run() {
		System.out.println("--------run()--------");
		
		/**执行日志*/
		SimpleDateFormat logFormater = new SimpleDateFormat("yyMMdd HHmmss");
		
		TParm logParm = new TParm();
		
		String resultMsg = "";//错误信息
		String detialMsg = "";//详细错误描述信息
		int falseCount =0;//错误数		
		int totalCount =0;//总数
		
		try{
			logParm.setData("BATCH_CODE","OPD_ORDER同步");
			logParm.setData("START_TIME",logFormater.format(new Date()));
			
			SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat formater2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			Timestamp date = SystemTool.getInstance().getDate();
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.add(Calendar.DATE, -1);	
			String dateStr = formater.format(cal.getTime());			
						
			TParm parm = new TParm();
			
			parm.setData("ORDER_DATE",dateStr);
			
			TParm result = SPCOpdOrderPathTool.getInstance().query(parm);
			
			int count = result.getCount();
			totalCount = count; 
			
			//if("2013-01-24".equals("2013-01-24")) throw new Exception();//测试
			
			for(int i = 0; i < count; i++){
				
				String case_no = "";
				String rx_no = "";
				String seq_no = "";
				
				try{
					case_no = result.getValue("CASE_NO", i);
					rx_no = result.getValue("RX_NO", i);
					seq_no = result.getValue("SEQ_NO", i);
					
					String pha_check_code = result.getValue("PHA_CHECK_CODE", i);
					
					Object obj1 = result.getData("PHA_CHECK_DATE", i);
					Date pha_check_date = null;
					if(null != obj1) pha_check_date = (Date)obj1;
					
					String pha_check_date_str = null;
					if(null != pha_check_date) pha_check_date_str = formater2.format(pha_check_date);
					
					String pha_dosage_code = result.getValue("PHA_DOSAGE_CODE", i);
					
					Object obj2 = result.getData("PHA_DOSAGE_DATE", i);
					Date pha_dosage_date = null;
					if(null != obj2) pha_dosage_date = (Date)obj2;
					
					String pha_dosage_date_str = null;
					if(null != pha_dosage_date) pha_dosage_date_str = formater2.format(pha_dosage_date);
					
					String pha_dispense_code = result.getValue("PHA_DISPENSE_CODE", i);
					
					Object obj3 = result.getData("PHA_DISPENSE_DATE", i);
					Date pha_dispense_date = null;
					if(null != obj3) pha_dispense_date = (Date)obj3;
					String pha_dispense_date_str = null;
					if(null != pha_dispense_date) pha_dispense_date_str = formater2.format(pha_dispense_date);
					
					String pha_retn_code = result.getValue("PHA_RETN_CODE", i);
					
					Object obj4 = result.getData("PHA_RETN_DATE", i);
					Date pha_retn_date = null;
					if(null != obj4) pha_retn_date = (Date)obj4;
					String pha_retn_date_str = null;
					if(null != pha_retn_date) pha_retn_date_str = formater2.format(pha_retn_date);
					
					String batchSeq1 = result.getValue("BATCH_SEQ1", i);
					String verifyinPrice1 = result.getValue("VERIFYIN_PRICE1", i);
					
					String batchSeq2 = result.getValue("BATCH_SEQ2", i);
					String verifyinPrice2 = result.getValue("VERIFYIN_PRICE2", i);
					
					String batchSeq3 = result.getValue("BATCH_SEQ3", i);
					String verifyinPrice3 = result.getValue("VERIFYIN_PRICE3", i);
													
					boolean rtn = synchHis(case_no,rx_no,seq_no,
							pha_check_code,pha_check_date_str,
							pha_dosage_code,pha_dosage_date_str,
							pha_dispense_code,pha_dispense_date_str,
							pha_retn_code,pha_retn_date_str,
							batchSeq1,verifyinPrice1,batchSeq2,verifyinPrice2,batchSeq3,verifyinPrice3);	
					
					if(!rtn) {
						falseCount = falseCount + 1;
						detialMsg = detialMsg + case_no + "-" + rx_no + "-" + seq_no + "(ED),";//最大长度55
					}
					
				}catch (Exception e){
					e.printStackTrace();
					falseCount = falseCount + 1;
					detialMsg = detialMsg + case_no + "-" + rx_no + "-" + seq_no + ",";
				}
								
			}
		}catch (Exception e){
			e.printStackTrace();
			
			logParm.setData("END_TIME",logFormater.format(new Date()));				
			logParm.setData("RESULT_DESC","执行失败");
			
			SPCOpdOrderPathTool.getInstance().save(logParm);
			
			return false;
		}
		
		
		logParm.setData("END_TIME",logFormater.format(new Date()));
		
		if(null != detialMsg && detialMsg.length()>800){
			detialMsg = detialMsg.substring(0, 800);
		}	
		
		resultMsg="TC:" + totalCount + " FC:" + String.valueOf(falseCount) + "." + detialMsg;
		logParm.setData("RESULT_DESC",resultMsg);
		
		SPCOpdOrderPathTool.getInstance().save(logParm);
		
		return true;
	}
	
	/**同步方法*/
	private boolean synchHis(String case_no,String rx_no,String seq_no,
			String pha_check_code,String pha_check_date_str,
			String pha_dosage_code,String pha_dosage_date_str,
			String pha_dispense_code,String pha_dispense_date_str,
			String pha_retn_code,String pha_retn_date_str,
			String batchSeq1,String verifyinPrice1,
			String batchSeq2,String verifyinPrice2,
			String batchSeq3,String verifyinPrice3) throws Exception{
		
		boolean _updateOpdOrder__return = false;
		
		try{
	      
			 _updateOpdOrder__return = updateOpdOrder(case_no,rx_no,seq_no,
															pha_check_code,pha_check_date_str,
															pha_dosage_code,pha_dosage_date_str,
															pha_dispense_code,pha_dispense_date_str,
															pha_retn_code,pha_retn_date_str,
															batchSeq1,verifyinPrice1,
															batchSeq2,verifyinPrice2,
															batchSeq3,verifyinPrice3);
		}catch(Exception e){
			throw new Exception();
		}
      
      return _updateOpdOrder__return;
	}
	
	//更改为动态IP
	public boolean updateOpdOrder(String case_no,String rx_no,String seq_no,
			String pha_check_code,String pha_check_date_str,
			String pha_dosage_code,String pha_dosage_date_str,
			String pha_dispense_code,String pha_dispense_date_str,
			String pha_retn_code,String pha_retn_date_str,
			String batchSeq1,String verifyinPrice1,
			String batchSeq2,String verifyinPrice2,
			String batchSeq3,String verifyinPrice3)throws Exception {
		
		 String ip = getWebServicesIp();
	     String url = "http://"+ip+"/services/SPCOpdOrderSynchWs" ;
	     
	     JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean(); 
	     factory.setAddress(url); 
	     factory.setServiceClass(SPCOpdOrderSynchWsTool.class); 
	     SPCOpdOrderSynchWsTool service = (SPCOpdOrderSynchWsTool) factory.create();
	     boolean  result = service.updateOpdOrder(case_no,rx_no,seq_no,
					pha_check_code,pha_check_date_str,
					pha_dosage_code,pha_dosage_date_str,
					pha_dispense_code,pha_dispense_date_str,
					pha_retn_code,pha_retn_date_str,
					batchSeq1,verifyinPrice1,
					batchSeq2,verifyinPrice2,
					batchSeq3,verifyinPrice3);
	     return result ;
	    
	     
	}
	
	public static void main(String[] args) {
		System.out.println("hao-----"+"hao".length());
		System.out.println("好好-----"+"好好".length());
		System.out.println("。-----"+"。".length());
		System.out.println(".-----"+".".length());
		System.out.println("（-----"+"（".length());
		System.out.println("(-----"+"(".length());
		
	}

}
