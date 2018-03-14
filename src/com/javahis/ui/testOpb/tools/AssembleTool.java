package com.javahis.ui.testOpb.tools;

import java.math.BigDecimal;
import java.util.List;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.javahis.ui.testOpb.bean.OpdOrder;
import com.javahis.ui.testOpb.bean.OpdOrderHistoryNew;

public class AssembleTool {
	
private static AssembleTool instanceObject;
	
	public static AssembleTool getInstance() {
        if (instanceObject == null)
            instanceObject = new AssembleTool();
        return instanceObject;
    }
	

	
	public TParm parmToSql(TParm inParm){
		System.out.println("opdOrderHistoryNew----2.1---"+SystemTool.getInstance().getDate());

		String LastHistoryIds="";
		TParm orderParm = inParm.getParm("orderParm");
		TParm historyParm = this.getOpdOrderHistory(orderParm.getValue("CASE_NO", 0));
		TParm parm = new TParm();
		TParm sqlParm = new TParm();
		List<OpdOrder> list;
		try {
			list = QueryTool.getInstance().queryByParm(orderParm, new OpdOrder());

			OpdOrderHistoryNew opdOrderHistoryNew;

			for (OpdOrder opdOrder : list) {

				opdOrderHistoryNew = new OpdOrderHistoryNew();

				opdOrderHistoryNew = QueryTool.getInstance().synClasses(opdOrder,
						opdOrderHistoryNew);

				String historyNo = SystemTool.getInstance().getNo("ALL", "ODO", "HISTORY_ID","HISTORY_ID");
				if (null==historyNo||historyNo.length()<=0) {
					historyNo = SystemTool.getInstance().getNo("ALL", "ODO", "HISTORY_ID","HISTORY_ID");// 得到医疗卡外部交易号
				}
				opdOrderHistoryNew.historyId = historyNo;
				opdOrderHistoryNew.activeFlg="Y";
				opdOrderHistoryNew.optType=inParm.getValue("OPT_TYPE");
				opdOrderHistoryNew.ektHistoryNo=inParm.getValue("EKT_HISTORY_NO");
				opdOrderHistoryNew.mzconfirmNo=inParm.getValue("MZCONFIRM_NO");
				
				opdOrderHistoryNew.opbRecpNo=opdOrderHistoryNew.receiptNo;

				opdOrderHistoryNew.optUser=inParm.getValue("OPT_USER");
				opdOrderHistoryNew.optTerm=inParm.getValue("OPT_TERM");
				opdOrderHistoryNew.optDate=QueryTool.getInstance().getSystemTime();
				opdOrderHistoryNew.opdOptTerm=inParm.getValue("OPT_TERM");
				opdOrderHistoryNew.opdOptUser=inParm.getValue("OPT_USER");
				opdOrderHistoryNew.opdOptDate=QueryTool.getInstance().getSystemTime();
				
				if("DELETE".equals(inParm.getValue("OPT_TYPE")) ||
						"UPDATE".equals(inParm.getValue("OPT_TYPE"))){
					
//					String lastHistoryId = getHistoryId(opdOrder.caseNo,opdOrder.rxNo,opdOrder.seqNo.toString());
					String lastHistoryId ="";
					for (int i = 0; i < historyParm.getCount("CASE_NO"); i++) {
						if(opdOrder.caseNo.equals(historyParm.getValue("CASE_NO", i)) &&
								opdOrder.rxNo.equals(historyParm.getValue("RX_NO", i))	&&
								opdOrder.seqNo.compareTo(new BigDecimal(historyParm.getValue("SEQ_NO", i))) ==0
								){
							 lastHistoryId =historyParm.getValue("HISTORY_ID", i);
							 break;
						}
					}
					
					opdOrderHistoryNew.lastHistoryId = lastHistoryId;
					if(lastHistoryId.length() > 0){
						LastHistoryIds = LastHistoryIds +"'"+lastHistoryId+"',";
					}
				}else{
					opdOrderHistoryNew.lastHistoryId = "";
				}

				opdOrderHistoryNew.optDate=QueryTool.getInstance().getSystemTime();
				
				opdOrderHistoryNew.modifyState=Type.INSERT;

				String sql2 = SqlTool.getInstance().getSql(opdOrderHistoryNew);
//				System.out.println(sql2);
				sqlParm.addData("SQL", sql2);
			}
			
			
//			System.out.println("LastHistoryIds---"+LastHistoryIds);

			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			System.out.println("opdOrderHistoryNew----2.2---"+SystemTool.getInstance().getDate());
			parm.setData("LastHistoryIds", LastHistoryIds);
			parm.setData("sqlParm", sqlParm.getData());
			return parm;
		}
	}
	
	private String getHistoryId(String caseNo,String rxNo,String seqNo){
		String sql = "SELECT HISTORY_ID FROM OPD_ORDER_HISTORY_NEW WHERE CASE_NO='"+caseNo+"' " +
				" AND RX_NO='"+rxNo+"' AND SEQ_NO='"+seqNo+"' ORDER BY OPT_DATE DESC";
//		System.out.println("getHistoryId-----"+sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));;
		if(parm.getCount() > 0){
			return parm.getValue("HISTORY_ID", 0);
		}else{
			return "";
		}
		
	}
	
	private TParm getOpdOrderHistory(String caseNo){
		String sql="SELECT MAX(HISTORY_ID) HISTORY_ID,CASE_NO,RX_NO,SEQ_NO FROM OPD_ORDER_HISTORY_NEW WHERE CASE_NO='"+caseNo+"'  GROUP BY CASE_NO,RX_NO,SEQ_NO";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
		
	}

}
