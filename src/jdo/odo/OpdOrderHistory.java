package jdo.odo;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;

/**
*
* <p>Title: 医嘱存储对象</p>
*
* <p>Description: </p>
*
* <p>Copyright: Copyright (c) 2008</p>
*
* <p>Company: JavaHis</p>
*
* @author ehui 2009.2.11
* @version 1.0
*/
public class OpdOrderHistory extends StoreBase {
	private static final String INIT="SELECT * FROM OPD_ORDER_HISTORY_NEW WHERE RX_NO='#'";
	private String dateNames="ORDER_DATE;DC_ORDER_DATE;SENDATC_DATE;BILL_DATE;PHA_CHECK_DATE;PHA_DOSAGE_DATE;PHA_DISPENSE_DATE;" +
			"PHA_RETN_DATE;NS_EXEC_DATE;SEND_DCT_DATE;DECOCT_DATE;SEND_ORG_DATE;EXM_EXEC_END_DATE;BIRTH_DATE;PICK_TMIE;RECLAIM_DATE";
	private List<String> dateList = new ArrayList<String>();
	
	private String LastHistoryIds="";
	
	public String getLastHistoryIds() {
		return LastHistoryIds;
	}
	public void setLastHistoryIds(String lastHistoryIds) {
		LastHistoryIds = lastHistoryIds;
	}
	/**
	 * 查询
	 */
	public boolean onQuery(){
		
		this.setSQL(INIT);
		this.retrieve();
		
		String[] names= dateNames.split(";");
		dateList = new ArrayList<String>();
		for (int i = 0; i < names.length; i++) {
			if(!dateList.contains(names[i])){
				dateList.add(names[i]);
			}
		}
		
		return true;
	}
	/**
	 * 根据给入的order对象插入
	 * @param order
	 * @return
	 */
	public boolean insert(TParm parm){
		if(parm==null){
			return false;
		}
		if(parm.getCount()<0){
			return false;
		}
		int count=parm.getCount();
		String[] names=parm.getNames();
		int countName=names.length;
		Timestamp now=this.getDBTime();
		//System.out.println("insert.count="+count);
		for(int i=0;i<count;i++){
			int row=this.insertRow();
			for(int j=0;j<countName;j++){				
				if(names[j].equals("OPT_USER")){
					if(parm.getValue(names[j],i).length() == 0){
						
						if("UPDATE".equals(parm.getValue("OPT_TYPE"))){
							this.setItem(row, "OPD_OPT_USER", parm.getValue("OPT_USER"));
						}else{
							this.setItem(row, "OPD_OPT_USER", Operator.getID());
						}
						
					}else{
						this.setItem(row, "OPD_OPT_USER", parm.getValue(names[j],i));
					}
					
				}else if(names[j].equals("OPT_DATE")){
					if(parm.getValue(names[j],i).length() == 0){
						this.setItem(row, "OPD_OPT_DATE", now);
					}else{
						this.setItem(row, "OPD_OPT_DATE", stringToTimestamp(parm.getValue("OPT_DATE", i)));
					}
					
				}else if(names[j].equals("OPT_TERM")){
					if(parm.getValue(names[j],i).length() == 0){
						
						if("UPDATE".equals(parm.getValue("OPT_TYPE"))){
							this.setItem(row, "OPD_OPT_TERM", parm.getValue("OPT_TERM"));
						}else{
							this.setItem(row, "OPD_OPT_TERM", Operator.getIP());
						}
						
					}else{
						this.setItem(row, "OPD_OPT_TERM", parm.getValue(names[j],i));
					}
					
				}else if(dateList.contains(names[j])){
					if(parm.getValue(names[j], i).length() > 0){
						this.setItem(row, names[j], stringToTimestamp(parm.getValue(names[j],i)));
					}else{
						this.setItem(row, names[j], "");
					}
				}else{
					this.setItem(row, names[j], parm.getValue(names[j],i));
				}
			}
			
			String historyNo = SystemTool.getInstance().getNo("ALL", "ODO", "HISTORY_ID","HISTORY_ID");
			if (null==historyNo||historyNo.length()<=0) {
				historyNo = SystemTool.getInstance().getNo("ALL", "ODO", "HISTORY_ID","HISTORY_ID");// 得到医疗卡外部交易号
			}
			this.setItem(row, "HISTORY_ID", historyNo);
			this.setItem(row, "ACTIVE_FLG", "Y");
			
			this.setItem(row, "OPT_TYPE", parm.getValue("OPT_TYPE"));
			this.setItem(row, "EKT_HISTORY_NO", parm.getValue("EKT_HISTORY_NO"));
			this.setItem(row, "MZCONFIRM_NO", parm.getValue("MZCONFIRM_NO"));
			this.setItem(row, "OPB_RECP_NO", this.getItemData(row, "RECEIPT_NO"));
			 
			if("DELETE".equals(parm.getValue("OPT_TYPE")) ||
					"UPDATE".equals(parm.getValue("OPT_TYPE"))){
				String lastHistoryId = getHistoryId(this.getItemString(row, "CASE_NO"),this.getItemString(row, "RX_NO"),this.getItemString(row, "SEQ_NO"));
				this.setItem(row, "LAST_HISTORY_ID", lastHistoryId);
				if(lastHistoryId.length() > 0){
					LastHistoryIds = LastHistoryIds +"'"+lastHistoryId+"',";
				}
			}else{
				this.setItem(row, "LAST_HISTORY_ID", "");
			}
			
//			this.setItem(row, "DC_ORDER_DATE", StringTool.getString(now,"yyyyMMddHHmmss"));
////			Timestamp orderDate=TypeTool.getTimestamp( this.getItemData(row, "ORDER_DATE"));
////			orderDate=StringTool.getTimestamp(StringTool.getString(orderDate, "yyyyMMddHHmmss"),"yyyy/MM/dd HH:mm:ss");
//			String odStr = this.getItemData(row, "ORDER_DATE")+"";
//			String bdStr = this.getItemData(row, "BILL_DATE")+"";
//			
//			
////			Timestamp billDate=TypeTool.getTimestamp( this.getItemData(row, "BILL_DATE"));
////			billDate=StringTool.getTimestamp(StringTool.getString(billDate, "yyyyMMddHHmmss"),"yyyy/MM/dd HH:mm:ss");
//			
//			this.setItem(row, "DC_DR_CODE", Operator.getID());
//			if(odStr.length() > 0){
//				this.setItem(row, "ORDER_DATE", stringToTimestamp(odStr));
//			}else{
//				this.setItem(row, "ORDER_DATE", "");
//			}
//			if(bdStr.length() > 0){
//				this.setItem(row, "BILL_DATE", stringToTimestamp(bdStr));
//			}else{
//				this.setItem(row, "BILL_DATE", "");
//			}
//			//add by huangtt 20150605 start 护士执行医嘱时间格式修改
//			String nsExecDate = this.getItemData(row, "NS_EXEC_DATE")+""; 
//			if(nsExecDate.length() > 0){
//				this.setItem(row, "NS_EXEC_DATE", stringToTimestamp(nsExecDate));
//			}else{
//				this.setItem(row, "NS_EXEC_DATE", "");
//			} 
//			//add by huangtt 20150605 end
//			this.setItem(row, "DC_DEPT_CODE", Operator.getDept());
			
			if("UPDATE".equals(parm.getValue("OPT_TYPE"))){
				this.setItem(row, "OPT_USER", parm.getValue("OPT_USER"));				
				this.setItem(row, "OPT_TERM", parm.getValue("OPT_TERM"));
			}else{
				this.setItem(row, "OPT_USER", Operator.getID());
				this.setItem(row, "OPT_TERM", Operator.getIP());
			}

			this.setItem(row, "OPT_DATE", now);
			this.setActive(row,true);
		}
		return true;
	}
	
	private String getHistoryId(String caseNo,String rxNo,String seqNo){
		String sql = "SELECT HISTORY_ID FROM OPD_ORDER_HISTORY_NEW WHERE CASE_NO='"+caseNo+"' " +
				" AND RX_NO='"+rxNo+"' AND SEQ_NO='"+seqNo+"' ORDER BY OPT_DATE DESC";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));;
		if(parm.getCount() > 0){
			return parm.getValue("HISTORY_ID", 0);
		}else{
			return "";
		}
		
	}
	
	private Timestamp stringToTimestamp(String tsStr){
	    Timestamp ts = new Timestamp(System.currentTimeMillis());  
        try {  
        	tsStr = tsStr.substring(0, 19);
            ts = Timestamp.valueOf(tsStr);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }
        return ts;
	}
}
