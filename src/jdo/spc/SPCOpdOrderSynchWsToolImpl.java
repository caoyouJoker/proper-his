package jdo.spc;

import javax.jws.WebService;
import com.dongyang.Service.Server;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;


/**
 * <p>Title: 物联网与HIS OPD_ORDER表同步 werbservice接口</p>
 *
 * <p>Description: 物联网与HIS OPD_ORDER表同步 werbservice接口</p>
 *
 * <p>Copyright: Copyright (c) 2012</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author liuzhen 2013-1-21
 * @version 4.0
 */

@WebService
public class SPCOpdOrderSynchWsToolImpl implements SPCOpdOrderSynchWsTool {
	
	
	
	/**更新 OPD_ORDER 接口*/	
	@Override
	public boolean updateOpdOrder(String case_no,String rx_no,String seq_no,
									String pha_check_code,String pha_check_date_str,
									String pha_dosage_code,String pha_dosage_date_str,
									String pha_dispense_code,String pha_dispense_date_str,
									String pha_retn_code,String pha_retn_date_str,
									String batchSeq1,String verifyinPrice1,
									String batchSeq2,String verifyinPrice2,
									String batchSeq3,String verifyinPrice3){
		try{
			
			String con1 = "";
			if(null!= pha_check_date_str && !"".equals(pha_check_date_str.trim())) {
				con1 = "PHA_CHECK_DATE = TO_DATE('"+pha_check_date_str+"','yyyy-mm-dd hh24:mi:ss'), ";
			}
			
			String con2 = "";
			if(null!= pha_dosage_code && !"".equals(pha_dosage_code.trim())) {
				con2 = "PHA_DOSAGE_CODE = '"+pha_dosage_code+"', ";
			}
			
			String con3 = "";
			if(null!= pha_dosage_date_str && !"".equals(pha_dosage_date_str.trim())) {
				con3 = "PHA_DOSAGE_DATE = TO_DATE('"+pha_dosage_date_str+"','yyyy-mm-dd hh24:mi:ss'), ";
			}
			
			String con4 = "";
			if(null!= pha_dispense_code && !"".equals(pha_dispense_code.trim())) {
				con4 = "PHA_DISPENSE_CODE = '"+pha_dispense_code+"', ";
			}
			
			String con5 = "";
			if(null!= pha_dispense_date_str && !"".equals(pha_dispense_date_str.trim())) {
				con5 = "PHA_DISPENSE_DATE = TO_DATE('"+pha_dispense_date_str+"','yyyy-mm-dd hh24:mi:ss'), ";
			}
			
			String con7 = "";
			if(null!= pha_retn_date_str && !"".equals(pha_retn_date_str.trim())) {
				con7 = "PHA_RETN_DATE = TO_DATE('"+pha_retn_date_str+"','yyyy-mm-dd hh24:mi:ss'), ";
			}
			
			String con8 = "";
			if(null != batchSeq1 && !"".equals(batchSeq1.trim())) {
				con8 = "BATCH_SEQ1 = " + batchSeq1.trim() + ", ";
			}
			String con9 = "";
			if(null != verifyinPrice1 && !"".equals(verifyinPrice1.trim())) {
				con9 = "VERIFYIN_PRICE1 = " + verifyinPrice1.trim() + ", ";
			}
			
			String con10 = "";
			if(null != batchSeq2 && !"".equals(batchSeq2.trim())) {
				con10 = "BATCH_SEQ2 = " + batchSeq2.trim() + ", ";
			}
			String con11 = "";
			if(null != verifyinPrice2 && !"".equals(verifyinPrice2.trim())) {
				con11 = "VERIFYIN_PRICE2 = " + verifyinPrice2.trim() + ", ";
			}
			
			String con12 = "";
			if(null != batchSeq3 && !"".equals(batchSeq3.trim())) {
				con12 = "BATCH_SEQ3 = " + batchSeq3.trim() + ", ";
			}
			String con13 = "";
			if(null != verifyinPrice3 && !"".equals(verifyinPrice3.trim())) {
				con13 = "VERIFYIN_PRICE3 = " + verifyinPrice3.trim() + ", ";
			}
			
			String sql = "UPDATE  OPD_ORDER " +
							"SET PHA_CHECK_CODE = '"+pha_check_code+"', " +
								con1 +
								con2 +
								con3 +
								con4 +
								con5 +
								con7 +
								con8 +
								con9 +
								con10 +
								con11 +
								con12 +
								con13 +
								" PHA_RETN_CODE = '"+pha_retn_code+"' " +
						"WHERE " +
							"CASE_NO = '"+case_no+"' " +
						"AND RX_NO = '"+rx_no+"' " +
						"AND SEQ_NO = "+seq_no+"";			
				
				Server.autoInit(this);
				
				TParm result = new TParm(TJDODBTool.getInstance().update(sql));

				if(result.getErrCode() < 0 || (Integer)result.getData("RETURN")==0) return false;
				
				return true;
				
			
		}catch(Exception e){
			return false;
		}

		
	}
	
}


