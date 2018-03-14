package jdo.med;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import jdo.sys.SystemTool;
import jdo.util.XmlUtil;

import com.dongyang.config.TConfigParm;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.db.TDBPoolManager;
import com.dongyang.util.StringTool;

public class MedSmsTimerTask {
	
	
	private String configDir = null;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	private TConnection  connection  = null; 
	
    ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
    @SuppressWarnings("unchecked")
	ScheduledFuture  taskHandle =  null;
	
	public MedSmsTimerTask(){
		
	}
	public MedSmsTimerTask(String configDir){
		this.configDir = configDir;
	}
	
	public TConnection getTConnection(){
		
		if(connection == null ){
			TDBPoolManager.getInstance().init(configDir + "WEB-INF\\config\\system\\");
			TConfigParm parm1 = new TConfigParm();
			parm1.setSystemGroup("");
			if(!configDir.endsWith("\\"))
		            configDir += "\\";
		    configDir += "WEB-INF\\config\\";
		    String dir = configDir + "system\\";
			parm1.setConfig(dir+"TDBInfo.x");
			parm1.setConfigClass(dir+"TClass.x");
			TDBPoolManager m = new TDBPoolManager();
			m.init(parm1);
			connection = m.getConnection("javahis");
		}
		return connection;
	}
	
	/**
	 * 定时扫描MED_SMS表
	 */
	public  void doJob(){
		//虽然抛出了运行异常,当被拦截了,next周期继续运行
		taskHandle =  exec.scheduleAtFixedRate(new Runnable() {
            public void run() {
                try{
                	TParm medSmsParm = MedSmsTool.getInstance().getMinute();
           		 
            		if(medSmsParm  != null && medSmsParm.getCount() > 0 ) {
            			/**根据返回值与时间对比判断给哪一级主管发信息**/
            			
            			for(int i = 0 ; i< medSmsParm.getCount();i++ ){
            				
            				long minute = getDiffTime(medSmsParm.getValue("SEND_TIME",i));
            				
            				String caseNo = medSmsParm.getValue("CASE_NO",i);
            				String deptCode = medSmsParm.getValue("DEPT_CODE",i);
            				TParm telParm = null ;
            				//System.out.println("minute:"+minute);
            				TParm xmlParm = new TParm();
            				xmlParm.setData("SysNo","MED");
            				xmlParm.setData("Content",medSmsParm.getValue("SMS_CONTENT",i));
            			    xmlParm.setData("MrNo",medSmsParm.getValue("MR_NO",i));
            			    
            			    String admType = medSmsParm.getValue("ADM_TYPE",i);
            			    TParm searchParm = new TParm();
            				searchParm.setData("DEPT_CODE",deptCode);
            				TParm dutyParm = MedSmsTool.getInstance().getDutyTel(searchParm);
            				String deptChnCode = "";
            				if(dutyParm.getCount()> 0){
            					deptChnCode = dutyParm.getRow(0).getValue("DEPT_CHN_DESC") ;
            				}
            			    String patName = medSmsParm.getValue("PATIENT_NAME",i)+","+deptChnCode+","+getAdmType(admType);
            			    xmlParm.setData("Name",patName);
            				/**30分钟之内科室主任**/ 
            				if( (minute >=30) && ("".equals(medSmsParm.getValue("NOTIFY_DIRECTOR_DR_TIME", i))) ){//modify by guoy 20151021
            					
            					/**
            					String admType = medSmsParm.getValue("ADM_TYPE",i);
            					if(admType.equals("O") || admType.equals("E")){
            						telParm = MedSmsTool.getInstance().getDeanOrCompementTel(deptCode, "3");
            					}else if(admType.equals("I")){
            						telParm = MedSmsTool.getInstance().getDirectorTel(caseNo);
            					}*/
            					//住院、门诊科室都查设置表
            					telParm = MedSmsTool.getInstance().getDeanOrCompementTel(deptCode, "3");
            					XmlUtil.createSmsFile(xmlParm,telParm);
            					MedSmsTool.getInstance().updateMedSmsTime(medSmsParm.getValue("SMS_CODE",i), "NOTIFY_DIRECTOR_DR_TIME");
            				}
            				
            				/**40分钟之内医务科主管**/
            				if( (minute >=40) && ("".equals(medSmsParm.getValue("NOTIFY_COMPETENT_TIME", i))) ){//modify by guoy 20151021
            					
            					
            					telParm = MedSmsTool.getInstance().getDeanOrCompementTel(deptCode, "2");
            					 
            					XmlUtil.createSmsFile(xmlParm,telParm);
            					MedSmsTool.getInstance().updateMedSmsTime(medSmsParm.getValue("SMS_CODE",i), "NOTIFY_COMPETENT_TIME");
            				}
            				
            				/**50分钟之内主管院长**/
            				if( (minute >=50) && ("".equals(medSmsParm.getValue("NOTIFY_DEAN_TIME", i))) ){//modify by guoy 20151021
            					
            					telParm = MedSmsTool.getInstance().getDeanOrCompementTel(deptCode, "1");
            					 
            					XmlUtil.createSmsFile(xmlParm,telParm);
            					MedSmsTool.getInstance().updateMedSmsTime(medSmsParm.getValue("SMS_CODE",i), "NOTIFY_DEAN_TIME");
            				}
            			}
            		}
            		if(connection != null ){
            			connection.close();
            		} 
                }catch (Exception e){
                   e.printStackTrace();
                }
            }
        }, 10*1000, 60*1000, TimeUnit.MILLISECONDS);
	}
	
	public void close(){
		 exec.schedule(new Runnable() {   
            public void run() {   
               System.out.println("取消任务");   
                taskHandle.cancel(true);   
            }   
		 }, 60, TimeUnit.SECONDS);   
	}
	

	/**
	 * 两时间之差分钟
	 * @param medSms
	 * @return
	 */
	private long getDiffTime(String sendTime) {
		
		 //DateUtil.getNowTime(TIME_FORMAT);   
	    String systemTime = StringTool.getString(SystemTool.getInstance().getDate(), TIME_FORMAT);
		
		Date begin = null;
		Date end = null;
		try {
			end = sdf.parse(systemTime);
			begin = sdf.parse(sendTime);
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/**秒**/
		long between=(end.getTime()-begin.getTime())/1000;
		
		/**分钟**/
		long minute=between/60;
		return minute;
	}   
	
	private String getAdmType(String admType) {
		String admTypeChn = "";
		if(admType != null ){
			 if(admType.equals("O")){
				 admTypeChn = "门诊";
			 }else if(admType.equals("I")) {
				 admTypeChn = "住院";
			}else if(admType.equals("E")){
				 admTypeChn = "急诊";
			}else if(admType.equals("H")){
				 admTypeChn = "健康检查";
			}
		 }
		return admTypeChn;
	}
	
	 

	
	public static void main(String[] args) {
		TParm parm = new TParm();
		parm.setData("Tel", "18699999999");
		parm.setData("Content", "短信内容");
		parm.setData("MrNo", "住院号");
		parm.setData("CaseNo", "就诊号");
		parm.setData("Name", "姓名");
		//createSmsXml(parm);
		 SimpleDateFormat sdff = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String d = "2012-06-15 11:20:36";
		try {
			System.out.println(sdff.parse(d));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		MedSmsTimerTask mtt = new MedSmsTimerTask();
		long t = mtt.getDiffTime("2012-06-15 11:20:36");
		System.out.println(t);
	}

}
