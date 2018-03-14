package action.spc;

import java.text.SimpleDateFormat;
import java.util.Date;

import jdo.bil.BILSysParmTool;
import jdo.spc.INDSQL;
import jdo.ind.INDTool;
import jdo.sys.SystemTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;

/** 
 * <p>
 * Title: 根据结算数据自动生成请领数据
 * </p>
 * 
 * <p>
 * Description: 根据结算数据自动生成请领数据
 * </p>
 * 
 * <p>  
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author fuwj 2013.07.17
 * @version 1.0
 */
public class INDCreateRequestAutoAction extends TAction {

	/**
	 * 根据结算数据自动生成请领数据
	 * 
	 * @param parm
	 * @return
	 */
	public TParm createIndRequsestAuto(TParm parm) {
		TConnection conn = getConnection();
        TParm sysParm = BILSysParmTool.getInstance().getDayCycle("I");
        int monthCycle = sysParm.getInt("MONTH_CYCLE", 0);
        String closeDate = "";
        closeDate = StringTool.getString(parm.getTimestamp("CLOSE_DATE"), "yyyyMM") + monthCycle;
		TParm searchresult = INDTool.getInstance().queryOrgCodeInIndAccout(closeDate);//wanglong modify 20150202
		if (searchresult.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return searchresult;
		}
		int count = searchresult.getCount();
		String optUser = parm.getValue("OPT_USER");
		String optTerm = parm.getValue("OPT_TERM");
		String regionCode = parm.getValue("REGION_CODE");
		TParm result = new TParm();
		if (null != searchresult && count > 0) {				
			for (int i = 0; i < count; i++) {
				String appOrgCode = (String) searchresult.getData("ORG_CODE", i);	
				// String toOrgCode = "040101";  
				// 得到请领单号
				String requestNo = SystemTool.getInstance().getNo("ALL", "IND",
						"IND_REQUEST", "No");
				// 创建请领主档
				TParm requestM = INDTool.getInstance().createRequestM("040101",
						requestNo, appOrgCode, optUser, optTerm, regionCode,
						"1", "1");
				if (requestM.getErrCode() < 0) {
					conn.rollback();
					conn.close();
					return requestM;
				}
				// 生成请领主档
//				Date d = new Date();
//		        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
//		        String dateNowStr = sdf.format(d);   
//		        dateNowStr = dateNowStr.substring(0, 7)+"-25 23:59:59";
//		        requestM.setData("REQUEST_DATE", dateNowStr);
                requestM.setData("REQUEST_DATE",
                                 StringTool.getString(parm.getTimestamp("CLOSE_DATE"), "yyyy-MM-")
                                         + monthCycle + " 23:59:59");//wanglong add 20150202
			    result = new TParm(TJDODBTool.getInstance().update(
						INDSQL.createReRequestMAcnt(requestM), conn));
				if (result.getErrCode() < 0) {
					conn.rollback();
					conn.close();
					return result;
				}
				int seqNo = 1;
				// 查询物联网结算数据         
				//fux modify 20150630  and jdo/ind/INDSQL/getIndAccount  
				//修改使结算自动申请正常
				TParm parmD = INDTool.getInstance().queryIndAccout(closeDate,
						appOrgCode, "");
				if (parmD.getErrCode() < 0) {
					conn.rollback();  
					conn.close();
					return parmD;
				}
				//fux modify 20150710
				//ORDER BY A.CLOSE_DATE, A.ORG_CODE, A.ORDER_CODE,OUT_QTY
				double qtyChoose = 0;
				// 生成请领明细档
				for (int j = 0; j < parmD.getCount("ORDER_CODE"); j++) {
					String orderCode = parmD.getValue("ORDER_CODE", j);
					double qty = parmD.getDouble("OUT_QTY", j);
					if (qty == 0) {		
						continue;
					}						
					/*TParm phaParm = new TParm(TJDODBTool.getInstance().select(
		                      INDSQL.getPHABaseInfo(parmD.getValue("ORDER_CODE", j))));*/
					/* TParm supParm = new TParm(TJDODBTool.getInstance().select(INDSQL.getOrderCodeBySup("18", parmD.getValue("ORDER_CODE", j))));
					 if(supParm.getCount()<=0 ||supParm.getErrCode()<0) {
							System.out.println("");		  
					 }
					//中包装					    
					int conversionTraio = supParm.getInt("CONVERSION_RATIO", 0);
					conversionTraio = conversionTraio == 0 ? 1 : conversionTraio;*/
					seqNo = seqNo + 1;				
					//负数 退货票
					// 得到请领明细档PARM
					/*TParm requestD = INDTool.getInstance()
							.getRequestDAutoOfDrugInfo(requestNo, seqNo + "",
									orderCode, qty*conversionTraio, optUser, optTerm,
									regionCode);*/
//					if (qty < 0) {
//						qtyChoose = qtyChoose + qty;  
//						continue;                   
//					}	             
//					if (qty > 0){      
//					    qty = qtyChoose + qty;
//					    qtyChoose = 0;
//					}  
					//这样以后负数就是退货     正数就是验收入库
					TParm requestD = INDTool.getInstance()
					.getRequestDAutoOfDrugInfo(requestNo, seqNo + "",
							orderCode, qty, optUser, optTerm,
							regionCode);  
					if (requestD.getErrCode() < 0) {  
						conn.rollback();                 
						conn.close();
						return requestD;
					}   
					// 保存		
					result = new TParm(TJDODBTool.getInstance().update(
							INDSQL.saveRequestDAutoOfDrugAcnt(requestD), conn));
					if (result.getErrCode() < 0) {
						conn.rollback();
						conn.close();
						return result;
					}
				}
			}
		}
		conn.commit();
		conn.close();
		return result;
	}

}
