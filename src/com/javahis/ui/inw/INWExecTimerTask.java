package com.javahis.ui.inw;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.root.client.SocketLink;


public class INWExecTimerTask extends TControl{
	private String configDir = null;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	
    ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
    @SuppressWarnings("unchecked")
	ScheduledFuture  taskHandle =  null;
    
    //护士站执行消息传送
    private SocketLink client;
	
	public INWExecTimerTask(){
		
	}
	public INWExecTimerTask(String configDir){
		this.configDir = configDir;
	}
	
	public  void doJob(){
		//虽然抛出了运行异常,当被拦截了,next周期继续运行
		taskHandle =  exec.scheduleAtFixedRate(new Runnable() {
            public void run() {
                try{
                	if(isOclock()){
                		TParm p = new TParm(TJDODBTool.getInstance().select(getSql()));
                		//System.out.println("hello world:"+p);
                		if(p.getCount()>0){
                			String mes = "INWEXEC";
                			for(int i = 0 ; i < p.getCount() ; i++){
                				mes += p.getValue("PAT_NAME", i)+","+p.getValue("COUNT", i)+";";
                			}
                			client = SocketLink.running("", "ONW", "ONW");
                    		client.sendMessage(Operator.getStation(), mes);
                		}
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
              //System.out.println("取消任务");   
               taskHandle.cancel(true);   
           }   
		 }, 60, TimeUnit.SECONDS);   
	}
	
	public boolean isOclock(){
		//Timestamp time = SystemTool.getInstance().getDate();
		//Date d = new Date(time.getTime());
		
		Date d = new Date();
		SimpleDateFormat myFmt = new SimpleDateFormat("mm");
		String mm = myFmt.format(d);
		return "00".equals(mm);
	}
	
	public String getSql(){
		Timestamp now = SystemTool.getInstance().getDate();
		String start = now.toString().replace("-", "/").substring(0, 19);
		
		//应执行结束时间
		long time = now.getTime();
		time += 60*60*1000;
		Timestamp nowAddOneHour = new Timestamp(time);
		String end = nowAddOneHour.toString().replace("-", "/").substring(0, 19);
		
		String dept = Operator.getDept();
		String station = Operator.getStation();
		String n = Operator.getID();
		
		String sql = "";
		sql += " SELECT Z.PAT_NAME,COUNT(*) AS COUNT FROM ( " ;
		
		sql += " SELECT " +
					" DISTINCT " +
					" P.PAT_NAME , " +
					" M.ORDER_DESC , " +
					" (TO_DATE(substr(D.ORDER_DATE || D.ORDER_DATETIME,1,12), 'yyyyMMddhh24mi')) AS NS_EXEC_DATE " + 
				" FROM " +
					" ADM_INP A , " + 
					" SYS_PATINFO P , " +
					" ODI_DSPND D, " +
					" ODI_DSPNM M, " +
					" SYS_BED B " +
				" WHERE" +
					" A.MR_NO = P.MR_NO " +
					" AND A.CASE_NO = D.CASE_NO " + 
					" AND A.CASE_NO = M.CASE_NO " +
					" AND D.ORDER_NO = M.ORDER_NO " + 
					" AND D.ORDER_SEQ = M.ORDER_SEQ " +
					" AND D.CASE_NO = M.CASE_NO " + 
					" AND M.CAT1_TYPE = 'PHA' " + 
					" AND D.NS_EXEC_DATE_REAL IS NULL " +
					" AND M.DSPN_KIND IN ('ST','F','UD') " +
			  		" AND TO_DATE (substr(D.ORDER_DATE || D.ORDER_DATETIME,1,12), 'yyyyMMddhh24mi') > to_date('"+start+"','yyyy-mm-dd hh24:mi:ss') " +
			  		" AND TO_DATE (substr(D.ORDER_DATE || D.ORDER_DATETIME,1,12), 'yyyyMMddhh24mi') < to_date('"+end+"','yyyy-mm-dd hh24:mi:ss') " +
			  		" AND A.DEPT_CODE = '"+dept+"' " +
			  		" AND A.STATION_CODE = '"+station+"' " +
			  		//add by yangjj 20160108增加未停用和
			  		" AND A.DS_DATE IS NULL " +
			  		" AND A.CASE_NO = B.CASE_NO " + 
	                " AND A.BED_NO = B.BED_NO " +
			  		" AND B.CASE_NO IS NOT NULL ";
		
		sql += " ) Z GROUP BY Z.PAT_NAME";
		
		//System.out.println("未来1小时未执行医嘱数量SQL："+sql);
		return sql;
			
	}
}
