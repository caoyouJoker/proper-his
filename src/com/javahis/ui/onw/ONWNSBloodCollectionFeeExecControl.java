package com.javahis.ui.onw;

import java.sql.Timestamp;

import jdo.odo.OpdOrderHistory;
import jdo.reg.Reg;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.ui.testOpb.tools.AssembleTool;
import com.javahis.util.OdoUtil;

/**
 * <p>Title: 门诊采血费执行科室更新</p>
 *
 * <p>Description: 门诊采血费执行科室更新</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author huangtt 2017-04-18
 * @version 4.0
 */
public class ONWNSBloodCollectionFeeExecControl  extends TControl{
	private TTable table;
	private TParm parmB = new TParm();
	
	public void onInit(){
		table = (TTable) this.getComponent("TABLE");
		dateInit();
		
	}
	
	 /**
     * 日期控件初始化
     */
    public void dateInit(){
        String DATE = StringTool.getString(SystemTool.getInstance().getDate(),"yyyyMMdd");
        this.setValue("DATE_S",StringTool.getTimestamp(DATE+"000000","yyyyMMddHHmmss"));
        this.setValue("DATE_E",StringTool.getTimestamp(DATE+"235959","yyyyMMddHHmmss"));
    }
    
    public void onQuery(){
    	parmB = new TParm();
    	 String whereSql = "";
    	 if(this.getValueString("MR_NO").length()>0){
             Pat pat = Pat.onQueryByMrNo(this.getValueString("MR_NO"));
             if(pat==null){
                 this.messageBox("E0008");
                 return;
             }
             this.setValue("MR_NO",pat.getMrNo());
             this.setValue("PAT_NAME",pat.getName());
             this.setValue("SEX_CODE",pat.getSexCode());
             String age = OdoUtil.showAge(pat.getBirthday(),
     				SystemTool.getInstance().getDate());
     		this.setValue("AGE", age);
             whereSql += " AND A.MR_NO = '"+this.getValueString("MR_NO")+"'";
         }
    	 
    	String date_s = getValueString("DATE_S");
 		String date_e = getValueString("DATE_E");
 		date_s = date_s.substring(0, date_s.lastIndexOf(".")).replace(":", "")
 		.replace("-", "").replace(" ", "");
 		date_e = date_e.substring(0, date_e.lastIndexOf(".")).replace(":", "")
 		.replace("-", "").replace(" ", "");
 		
 		whereSql += "  AND A.ORDER_DATE BETWEEN TO_DATE ('"+date_s+"', 'YYYYMMDDHH24MISS')"
    	 		+ " AND TO_DATE ('"+date_e+"', 'YYYYMMDDHH24MISS')";
 		
 		if(this.getValueString("CASE_NO").length() > 0){
 			 whereSql += " AND A.CASE_NO = '"+this.getValueString("CASE_NO")+"'";
 		}
 		
 		 if(this.getValueBoolean("checkYES")){
 			whereSql += " AND A.BILL_FLG='Y'";
 		 }
 		 
 		 if(this.getValueBoolean("checkNO")){
  			whereSql += " AND (A.BILL_FLG='N' OR A.BILL_FLG IS NULL)";
  		 }
    	 
    	 String sql = "SELECT 'N' FLG,A.CASE_NO,A.MR_NO, B.PAT_NAME,"
    	 		+ "C.CHN_DESC SEX_CODE,A.ORDER_CODE,A.ORDER_DESC,A.ORDER_DATE, A.AR_AMT,"
    	 		+ " CASE A.BILL_FLG WHEN 'Y' THEN '已收费' ELSE '未收费' END BILL_DESC,"
    	 		+ " A.RX_NO, A.SEQ_NO,D.REALDR_CODE,D.REALDEPT_CODE "
    	 		+ " FROM OPD_ORDER A, SYS_PATINFO B, SYS_DICTIONARY C, REG_PATADM D"
    	 		+ " WHERE A.RX_TYPE = '7'"
    	 		+ " AND A.NS_BLOOD_COLL_EXEC_FLG = 'Y'"
    	 		+ " AND A.MR_NO = B.MR_NO"
    	 		+ " AND A.CASE_NO = D.CASE_NO"
    	 		+ " AND C.GROUP_ID = 'SYS_SEX'"
    	 		+ " AND B.SEX_CODE = C.ID";
    	 sql = sql + whereSql+" ORDER BY A.CASE_NO,A.MR_NO,A.ORDER_DATE ";
    	 
    	 System.out.println("查询SQL---"+sql);
    	 String sql1 = "SELECT A.* FROM OPD_ORDER A WHERE A.RX_TYPE = '7' "
    	 		+ "AND A.NS_BLOOD_COLL_EXEC_FLG = 'Y' "
    			 + whereSql+" ORDER BY A.CASE_NO,A.MR_NO,A.ORDER_DATE ";
    	 parmB = new TParm(TJDODBTool.getInstance().select(sql1));
    	 TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
    	 
    	 if(parm.getCount() < 0){
    		 this.messageBox("没有查询数据 ");
    		 table.removeRowAll();
    		 return;
    	 }
    	 
    	 table.setParmValue(parm);
	
    }
    
    public void onSave(){
    	if(!this.getValueBoolean("checkYES")){
    		this.messageBox("请在已收费状态下执行数据");
    		return;
    	}
    	
    	table.acceptText();
    	TParm tableParm = table.getParmValue();
    	TParm parm = new TParm();
    	TParm updateParm = new TParm();
    	OpdOrderHistory opdOrderHistory = new OpdOrderHistory();
    	
    	for (int i = 0; i < tableParm.getCount("CASE_NO"); i++) {
    		
    		if(tableParm.getBoolean("FLG", i)){
    			
    			parm.addData("CASE_NO", tableParm.getValue("CASE_NO", i));
    			parm.addData("SEQ_NO", tableParm.getValue("SEQ_NO", i));
    			parm.addData("RX_NO", tableParm.getValue("RX_NO", i));
    			parm.addData("ORDER_CODE", tableParm.getValue("ORDER_CODE", i));
    			parm.addData("DEPT_CODE", tableParm.getValue("REALDEPT_CODE", i));
    			parm.addData("DR_CODE", tableParm.getValue("REALDR_CODE", i));
    			parm.addData("COST_CENTER_CODE", Operator.getDept());
    			parm.addData("EXEC_DEPT_CODE", Operator.getDept());
    			parm.addData("EXEC_DR_CODE", Operator.getID());
    			parm.addData("OPT_USER", Operator.getID());
    			parm.addData("OPT_TERM", Operator.getIP());
    			
    			for (int j = 0; j < parmB.getCount(); j++) {
					if(tableParm.getValue("CASE_NO", i).equals(parmB.getValue("CASE_NO", j)) &&
							tableParm.getValue("SEQ_NO", i).equals(parmB.getValue("SEQ_NO", j)) &&
							tableParm.getValue("RX_NO", i).equals(parmB.getValue("RX_NO", j)) &&
							tableParm.getValue("ORDER_CODE", i).equals(parmB.getValue("ORDER_CODE", j)) 
							){
						parmB.setData("DEPT_CODE", j, tableParm.getValue("REALDEPT_CODE", i));
						parmB.setData("DR_CODE", j, tableParm.getValue("REALDR_CODE", i));
						parmB.setData("COST_CENTER_CODE", j, Operator.getDept());
						parmB.setData("EXEC_DEPT_CODE", j, Operator.getDept());
						parmB.setData("EXEC_DR_CODE", j, Operator.getID());
//						parmB.setData("OPT_DATE", j, ""); 
	    				
						updateParm.addRowData(parmB, j);
						break;
					}
				}
	
    		}
			
		}
    	
    	if(parm.getCount("CASE_NO") < 0){
    		this.messageBox("没有要保存的数据 ");
    		return;
    	}

    	parm.setCount(parm.getCount("CASE_NO"));
    	
    	TParm inParm = new TParm();
    	String [] sql =null;
    	
//    	System.out.println("updateParm---"+updateParm);
    	
    	if(updateParm.getCount("CASE_NO") > 0){
    		updateParm.setCount(updateParm.getCount("CASE_NO"));
    		TParm inParm1 = new TParm();
    		inParm1.setData("orderParm", updateParm.getData());
    		inParm1.setData("EKT_HISTORY_NO", "");
    		inParm1.setData("OPT_TYPE", "UPDATE");
    		inParm1.setData("OPT_USER", Operator.getID());
    		inParm1.setData("OPT_TERM", Operator.getIP());
    		TParm historyParm = AssembleTool.getInstance().parmToSql(inParm1);
			TParm sqlParm = historyParm.getParm("sqlParm");
			String ids = historyParm.getValue("LastHistoryIds");
			
			if(sqlParm.getCount("SQL") > 0){
				sql = new String[sqlParm.getCount("SQL")];
				
				for (int i = 0; i < sqlParm.getCount("SQL"); i++) {
					sql[i]= sqlParm.getValue("SQL",i);
				}
				
				if(ids.length() > 0){
					String [] sqlTemp = new String[1];
					ids = ids.substring(0, ids.length()-1);
					sqlTemp[0]="UPDATE OPD_ORDER_HISTORY_NEW SET ACTIVE_FLG='N' WHERE HISTORY_ID IN ("+ids+")";
					sql = StringTool.copyArray(sql, sqlTemp);
				}

			}
    		
    	}
    	
    	inParm.setData("updateParm", parm.getData());
    	inParm.setData("SQL", sql);
    	
    	
    	TParm result = TIOM_AppServer.executeAction("action.opd.ODOAction",
				"onSaveBlood", inParm);
    	
    	if(result.getErrCode() < 0){
    		this.messageBox("保存失败");
    		return;
    	}
    	
    	this.messageBox("保存成功");
    	this.onQuery();
    	
    }
    
    public void onClear(){
    	 this.dateInit();
         this.clearValue("MR_NO;CASE_NO;onAll;PAT_NAME;SEX_CODE;AGE");
         this.setValue("checkYES", "Y");
         this.setValue("checkNO", "N");
         table.removeRowAll();
         parmB = new TParm();
    }
    
    public TParm getOpdOrder(String caseNo,String seqNo,String rxNo,String orderCode){
    	String sql = "SELECT * FROM OPD_ORDER WHERE CASE_NO='"+caseNo+"' "
    			+ "AND RX_NO='"+rxNo+"' "
    			+ "AND SEQ_NO='"+seqNo+"' "
    			+ "AND ORDER_CODE='"+orderCode+"'";
    	TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
    	return parm;
    	
    }
    
   
    
    /**
	 * 全选
	 */
	public void onSelAll() {
		table.acceptText();
		TParm parm = table.getParmValue();
		int rowCount = parm.getCount();
		for (int i = 0; i < rowCount; i++) {
			if (this.getTCheckBox("All").isSelected())
				parm.setData("FLG", i, "Y");
			else
				parm.setData("FLG", i, "N");
		}
		table.setParmValue(parm);
	}
	
	/**
	 * 得到TCheckBox
	 * 
	 * @param tag
	 *            String
	 * @return TCheckBox
	 */
	public TCheckBox getTCheckBox(String tag) {
		return (TCheckBox) this.getComponent(tag);
	}
	
    /**
     * 读卡
     */
    public void onRead() {
        // TParm patParm = jdo.ekt.EKTIO.getInstance().getPat();
        TParm patParm = jdo.ekt.EKTIO.getInstance().TXreadEKT();
        if (patParm.getErrCode() < 0) {
            this.messageBox(patParm.getErrName() + " " + patParm.getErrText());
            return;
        }
        this.setValue("MR_NO", patParm.getValue("MR_NO"));
        this.onQuery();
    }

    

}
